package com.ecotrip.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ecotrip.app.data.TipoTransporte
import com.ecotrip.app.ui.FormularioUiState
import com.ecotrip.app.ui.theme.EcoTripTheme

/**
 * Pantalla 1 — Configuración del viaje.
 * Es un composable SIN ESTADO propio (state hoisting): recibe el estado y expone lambdas de eventos.
 * Así queda aislada de ViewModel/DataStore y se puede previsualizar o probar por separado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    state: FormularioUiState,
    onNombreChange: (String) -> Unit,
    onTransporteChange: (TipoTransporte) -> Unit,
    onBajaHuellaChange: (Boolean) -> Unit,
    onDestinoChange: (String) -> Unit,
    onDuracionChange: (String) -> Unit,
    onViajeGrupalChange: (Boolean) -> Unit,
    onPlanificar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            // Colores del tema dinámico: cambian con el fondo de pantalla del sistema.
            TopAppBar(
                title = { Text("EcoTrip 2026", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onPlanificar,
                icon = { Icon(Icons.Filled.Check, contentDescription = null) },
                text = { Text("Planificar ruta") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // permite usar el formulario en horizontal
                .imePadding()
                .padding(PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 112.dp)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "¿A dónde vamos sin dejar huella?",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tus datos se guardan solos: puedes girar el teléfono o salir de la app sin perderlos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = state.nombreViajero,
                onValueChange = onNombreChange,
                label = { Text("Nombre del viajero") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.destino,
                onValueChange = onDestinoChange,
                label = { Text("Destino") },
                singleLine = true,
                isError = state.errorDestino != null,
                supportingText = { state.errorDestino?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.duracion,
                onValueChange = onDuracionChange,
                label = { Text("Duración en días") },
                singleLine = true,
                isError = state.errorDuracion != null,
                supportingText = { state.errorDuracion?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "¿Cómo te mueves?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            SelectorTransporte(
                seleccionado = state.transporte,
                onSeleccion = onTransporteChange
            )

            FilaInterruptor(
                titulo = "Solo rutas de baja huella",
                descripcion = "Descarta trayectos con alta emisión de carbono.",
                activo = state.soloBajaHuella,
                onCambio = onBajaHuellaChange
            )
            FilaInterruptor(
                titulo = "Viaje grupal",
                descripcion = "Viajas acompañado, no solo.",
                activo = state.esViajeGrupal,
                onCambio = onViajeGrupalChange
            )
        }
    }
}

/** Selección única del medio de transporte (componente de selección de Material 3). */
@Composable
private fun SelectorTransporte(
    seleccionado: TipoTransporte,
    onSeleccion: (TipoTransporte) -> Unit
) {
    val opciones = TipoTransporte.entries
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        opciones.forEachIndexed { indice, opcion ->
            SegmentedButton(
                selected = opcion == seleccionado,
                onClick = { onSeleccion(opcion) },
                shape = SegmentedButtonDefaults.itemShape(indice, opciones.size)
            ) {
                Text(opcion.etiqueta, maxLines = 1)
            }
        }
    }
}

/** Fila completa tocable (toggleable) con título, descripción e interruptor. */
@Composable
private fun FilaInterruptor(
    titulo: String,
    descripcion: String,
    activo: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = activo, role = Role.Switch, onValueChange = onCambio)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleSmall)
                Text(
                    descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // El toque lo gestiona la fila completa; el Switch solo refleja el estado.
            Switch(checked = activo, onCheckedChange = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormularioPreview() {
    EcoTripTheme {
        FormularioScreen(
            state = FormularioUiState(nombreViajero = "Ana", destino = "Galápagos", duracion = "5"),
            onNombreChange = {}, onTransporteChange = {}, onBajaHuellaChange = {},
            onDestinoChange = {}, onDuracionChange = {}, onViajeGrupalChange = {},
            onPlanificar = {}
        )
    }
}