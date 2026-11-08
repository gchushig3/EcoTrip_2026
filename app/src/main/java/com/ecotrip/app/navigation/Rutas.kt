package com.ecotrip.app.navigation

import kotlinx.serialization.Serializable

/*
 * Rutas fuertemente tipadas (Type-Safe Navigation).
 * Se declaran FUERA del grafo de navegación: el compilador verifica los tipos de los argumentos,
 * por lo que ya no existen strings como "resumen/{destino}" ni Bundles armados a mano.
 */

/** Pantalla 1: no necesita argumentos, por eso es un `object`. */
@Serializable
object FormularioRoute

/** Pantalla 2: exige obligatoriamente los tres argumentos, con su tipo, al navegar. */
@Serializable
data class ResumenRoute(
    val destino: String,
    val diasDuracion: Int,
    val esViajeGrupal: Boolean,
    val ticketCode: String        // ← NUEVO: no anulable (String, sin "?")
)
