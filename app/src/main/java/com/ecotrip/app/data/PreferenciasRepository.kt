package com.ecotrip.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Delegado de extensión: crea UNA sola instancia de DataStore por proceso (archivo "eco_trip_prefs").
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "eco_trip_prefs")

/**
 * Capa de datos: guarda y lee de forma asíncrona las preferencias globales del usuario.
 * Es la única clase que conoce DataStore; el resto de la app solo ve [PreferenciaViaje].
 */
class PreferenciasRepository(context: Context) {

    // Se usa applicationContext para no retener una Activity (evita fugas de memoria).
    private val dataStore = context.applicationContext.dataStore

    private object Claves {
        val NOMBRE = stringPreferencesKey("nombre_viajero")
        val TRANSPORTE = stringPreferencesKey("transporte")
        val BAJA_HUELLA = booleanPreferencesKey("solo_baja_huella")
    }

    /** Flujo reactivo: emite un nuevo [PreferenciaViaje] cada vez que cambia algo en disco. */
    val preferencias: Flow<PreferenciaViaje> = dataStore.data
        .catch { error ->
            // Si el archivo no se puede leer, se arranca con valores por defecto en vez de cerrar la app.
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            PreferenciaViaje(
                nombreViajero = prefs[Claves.NOMBRE].orEmpty(),
                transporte = TipoTransporte.desdeNombre(prefs[Claves.TRANSPORTE]),
                soloBajaHuella = prefs[Claves.BAJA_HUELLA] ?: false
            )
        }

    suspend fun guardarNombre(nombre: String) {
        dataStore.edit { it[Claves.NOMBRE] = nombre }
    }

    suspend fun guardarTransporte(transporte: TipoTransporte) {
        dataStore.edit { it[Claves.TRANSPORTE] = transporte.name }
    }

    suspend fun guardarSoloBajaHuella(valor: Boolean) {
        dataStore.edit { it[Claves.BAJA_HUELLA] = valor }
    }
}
