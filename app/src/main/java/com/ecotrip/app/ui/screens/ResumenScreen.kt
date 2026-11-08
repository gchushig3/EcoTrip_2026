package com.ecotrip.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ecotrip.app.data.PreferenciaViaje
import com.ecotrip.app.data.TipoTransporte
import com.ecotrip.app.navigation.ResumenRoute
import com.ecotrip.app.ui.theme.EcoTripTheme

/**
 * Pantalla 2 — Resumen de la ruta, con forma de boleto de viaje.
 * Recibe la ruta TIPADA (destino, días, grupal) y las preferencias persistidas.
 * Todos los colores salen de MaterialTheme.colorScheme (Material You), ninguno es hexadecimal fijo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    ruta: ResumenRoute,
    preferencias: PreferenciaViaje,
    onSalir: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Tu ruta", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    // Botón de salida: regresa al formulario limpiando la pila de navegación.
                    IconButton(onClick = onSalir) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al formulario")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onSalir,
                icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                text = { Text("Nueva ruta") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Boleto(ruta = ruta, preferencias = preferencias)
        }
    }
}

/** Boleto: parte superior con el destino, línea punteada "perforada" y parte inferior con los datos. */
@Composable
private fun Boleto(ruta: ResumenRoute, preferencias: PreferenciaViaje) {
    // Plantillas de cadena de Kotlin: se arma el texto con $variable y ${expresión}.
    val textoDias = "${ruta.diasDuracion} ${if (ruta.diasDuracion == 1) "día" else "días"}"
    val nombre = preferencias.nombreViajero.trim().ifEmpty { "Viajero sin nombre" }
    val perfil = if (preferencias.soloBajaHuella) "Solo rutas de baja huella" else "Rutas mixtas"
    val compania = if (ruta.esViajeGrupal) "Viaje grupal" else "Viaje individual"

    // Se sustituye el Surface anterior por el Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // --- Parte superior: destino ---
            Surface(color = MaterialTheme.colorScheme.tertiaryContainer, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Destino",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = ruta.destino,
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "$textoDias · ${preferencias.transporte.etiqueta}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            LineaPerforada()

            // --- Parte inferior: detalle de la configuración ---
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DatoFila("Viajero", nombre)
                DatoFila("Transporte", preferencias.transporte.etiqueta)
                DatoFila("Huella", perfil)
                DatoFila("Compañía", compania)
                DatoFila("Duración", textoDias)

                // NUEVO — línea exacta que debe verse en la captura
                Text(
                    text = "Código de Reserva: ${ruta.ticketCode}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )            }
        }
    }
}

/** Dato en dos columnas: etiqueta tenue a la izquierda, valor destacado a la derecha. */
@Composable
private fun DatoFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

/** Línea punteada que imita la perforación del boleto. */
@Composable
private fun LineaPerforada() {
    val color = MaterialTheme.colorScheme.outline
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)) {
        drawLine(
            color = color,
            start = Offset(24.dp.toPx(), 0f),
            end = Offset(size.width - 24.dp.toPx(), 0f),
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResumenPreview() {
    // Usamos el tema y pasamos datos de prueba (Mock data)
    EcoTripTheme(dynamicColor = false) {
        ResumenScreen(
            ruta = ResumenRoute(
                destino = "Galápagos",
                diasDuracion = 5,
                esViajeGrupal = true,
                ticketCode = "ECO-12345"
            ),
            preferencias = PreferenciaViaje(
                nombreViajero = "Ana Silva",
                transporte = TipoTransporte.TREN,
                soloBajaHuella = true
            ),
            onSalir = {}
        )
    }
}
