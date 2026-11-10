package com.ecotrip.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ecotrip.app.data.PreferenciasRepository
import com.ecotrip.app.data.TipoTransporte
import com.ecotrip.app.navigation.ResumenRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado completo del formulario. Es inmutable: la UI solo lo DIBUJA, nunca lo modifica.
 * (Flujo unidireccional: los eventos suben al ViewModel, el estado baja a la UI.)
 */
data class FormularioUiState(
    // Persistidos en DataStore (sobreviven al cierre total de la app)
    val nombreViajero: String = "",
    val transporte: TipoTransporte = TipoTransporte.TREN,
    val soloBajaHuella: Boolean = false,
    // Protegidos por SavedStateHandle (sobreviven a rotación y a Process Death)
    val destino: String = "",
    val duracion: String = "",
    val esViajeGrupal: Boolean = false,
    // Errores de validación (null = campo correcto)
    val errorDestino: String? = null,
    val errorDuracion: String? = null
) {
    // Caso 3: regla derivada del estado. Se recalcula en cada pulsación de tecla (UDF).
    val puedePlanificar: Boolean
        get() = nombreViajero.length >= 4 && destino.isNotBlank()
}

/** Errores visibles para un campo concreto; viven aparte porque son efímeros. */
private data class ErroresFormulario(
    val destino: String? = null,
    val duracion: String? = null
)

/**
 * ViewModel unificado: Single Source of Truth del formulario.
 *  - DataStore  -> preferencias globales permanentes.
 *  - SavedStateHandle -> campos temporales del formulario (destino, duración, viaje grupal).
 * AndroidViewModel recibe [Application] y [SavedStateHandle] automáticamente con viewModel().
 */
class EcoTripViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repositorio = PreferenciasRepository(application)

    // --- Estado temporal enlazado al contenedor de estado de Jetpack (SavedStateHandle) ---
    private val destino = savedStateHandle.getStateFlow(CLAVE_DESTINO, "")
    private val duracion = savedStateHandle.getStateFlow(CLAVE_DURACION, "")
    private val esViajeGrupal = savedStateHandle.getStateFlow(CLAVE_GRUPAL, false)

    /**
     * El nombre se escribe en caliente aquí (para que el cursor no salte al esperar el disco)
     * y se replica a DataStore en segundo plano. Mientras sea null se muestra lo guardado en disco.
     */
    private val nombreEditado = savedStateHandle.getStateFlow<String?>(CLAVE_NOMBRE, null)

    private val errores = MutableStateFlow(ErroresFormulario())

    /** Estado único que consume la UI; combina las 3 fuentes en tiempo real. */
    val uiState: StateFlow<FormularioUiState> = combine(
        repositorio.preferencias,
        nombreEditado,
        combine(destino, duracion, esViajeGrupal) { d, t, g -> Triple(d, t, g) },
        errores
    ) { prefs, nombre, (d, t, g), err ->
        FormularioUiState(
            nombreViajero = nombre ?: prefs.nombreViajero,
            transporte = prefs.transporte,
            soloBajaHuella = prefs.soloBajaHuella,
            destino = d,
            duracion = t,
            esViajeGrupal = g,
            errorDestino = err.destino,
            errorDuracion = err.duracion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FormularioUiState(
            // Si el proceso murió, el nombre editado también se recupera sin esperar al disco.
            nombreViajero = savedStateHandle[CLAVE_NOMBRE] ?: "",
            destino = savedStateHandle[CLAVE_DESTINO] ?: "",
            duracion = savedStateHandle[CLAVE_DURACION] ?: "",
            esViajeGrupal = savedStateHandle[CLAVE_GRUPAL] ?: false
        )
    )

    // ---------------------------- Eventos que suben desde la UI ----------------------------

    fun onNombreChange(valor: String) {
        val limpio = valor.take(MAX_NOMBRE)
        savedStateHandle[CLAVE_NOMBRE] = limpio
        viewModelScope.launch { repositorio.guardarNombre(limpio) }
    }

    fun onTransporteChange(valor: TipoTransporte) {
        viewModelScope.launch { repositorio.guardarTransporte(valor) }
    }

    fun onBajaHuellaChange(valor: Boolean) {
        viewModelScope.launch { repositorio.guardarSoloBajaHuella(valor) }
    }

    fun onDestinoChange(valor: String) {
        savedStateHandle[CLAVE_DESTINO] = valor.take(MAX_DESTINO)
        errores.update { it.copy(destino = null) }
    }

    fun onDuracionChange(valor: String) {
        // Solo dígitos y máximo 3 caracteres: evita letras, signos y desbordes de Int.
        savedStateHandle[CLAVE_DURACION] = valor.filter { it.isDigit() }.take(3)
        errores.update { it.copy(duracion = null) }
    }

    fun onViajeGrupalChange(valor: Boolean) {
        savedStateHandle[CLAVE_GRUPAL] = valor
    }

    /**
     * Valida las entradas sin riesgo de crash. Devuelve la ruta tipada lista para navegar,
     * o null si hay errores (que quedan publicados en el estado para que la UI los muestre).
     */
    fun validarYCrearRuta(ticketCode: String): ResumenRoute? {
        val destinoLimpio = (savedStateHandle[CLAVE_DESTINO] ?: "").trim()
        // toIntOrNull() devuelve null ante vacío/ruido; el operador ?: lo convierte en un valor controlado.
        val dias = (savedStateHandle[CLAVE_DURACION] ?: "").trim().toIntOrNull() ?: 0

        val errorDestino = if (destinoLimpio.isEmpty()) "Escribe a dónde viajas" else null
        val errorDuracion = if (dias !in 1..MAX_DIAS) "Indica entre 1 y $MAX_DIAS días" else null
        errores.value = ErroresFormulario(errorDestino, errorDuracion)

        if (errorDestino != null || errorDuracion != null) return null
        return ResumenRoute(
            destino = destinoLimpio,
            diasDuracion = dias,
            esViajeGrupal = savedStateHandle[CLAVE_GRUPAL] ?: false,
            ticketCode = ticketCode          // ← lo recibe y lo entrega en la ruta
        )
    }

    private companion object {
        const val CLAVE_DESTINO = "destino"
        const val CLAVE_DURACION = "duracion"
        const val CLAVE_GRUPAL = "viaje_grupal"
        const val CLAVE_NOMBRE = "nombre_editado"
        const val MAX_NOMBRE = 40
        const val MAX_DESTINO = 60
        const val MAX_DIAS = 365
    }
}
