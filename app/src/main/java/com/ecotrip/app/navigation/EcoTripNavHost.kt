package com.ecotrip.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ecotrip.app.data.PreferenciaViaje
import com.ecotrip.app.ui.EcoTripViewModel
import com.ecotrip.app.ui.screens.FormularioScreen
import com.ecotrip.app.ui.screens.ResumenScreen

/**
 * Único NavHost de la app. El ViewModel se crea aquí (alcance Activity) y se comparte
 * entre ambas pantallas: es la única fuente de verdad.
 */
@Composable
fun EcoTripNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: EcoTripViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = FormularioRoute,
        modifier = modifier
    ) {
        composable<FormularioRoute> {
            FormularioScreen(
                state = state,
                onNombreChange = viewModel::onNombreChange,
                onTransporteChange = viewModel::onTransporteChange,
                onBajaHuellaChange = viewModel::onBajaHuellaChange,
                onDestinoChange = viewModel::onDestinoChange,
                onDuracionChange = viewModel::onDuracionChange,
                onViajeGrupalChange = viewModel::onViajeGrupalChange,
                onPlanificar = {
                    // Solo navega si la validación produjo una ruta; el compilador exige los 3 argumentos.
                    viewModel.validarYCrearRuta(ticketCode = "ECO-2026-XYZ") // <- Se inyecta el código por defecto
                        ?.let { ruta -> navController.navigate(ruta) }
                }
            )
        }

        composable<ResumenRoute> { backStackEntry ->
            // Recupera los argumentos ya tipados (String, Int, Boolean), sin parsear strings.
            val ruta = backStackEntry.toRoute<ResumenRoute>()
            val salir = { navController.volverAlFormulario() }

            // El botón "atrás" del sistema se comporta igual que el botón de la barra superior.
            BackHandler(onBack = salir)

            ResumenScreen(
                ruta = ruta,
                preferencias = PreferenciaViaje(
                    nombreViajero = state.nombreViajero,
                    transporte = state.transporte,
                    soloBajaHuella = state.soloBajaHuella
                ),
                onSalir = salir
            )
        }
    }
}

/**
 * Regresa al formulario destruyendo el historial previo:
 *  - popUpTo(inclusive) elimina de la pila las instancias anteriores del formulario y del resumen.
 *  - launchSingleTop evita crear una segunda copia del formulario si ya está arriba.
 */
private fun NavHostController.volverAlFormulario() {
    navigate(FormularioRoute) {
        popUpTo<FormularioRoute> { inclusive = true }
        launchSingleTop = true
    }
}
