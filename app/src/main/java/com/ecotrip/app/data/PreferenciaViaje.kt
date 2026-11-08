package com.ecotrip.app.data

/**
 * Tipos de transporte ecológico disponibles.
 * Cada valor guarda la etiqueta que se muestra en la UI, para no repartir textos por las pantallas.
 */
enum class TipoTransporte(val etiqueta: String) {
    TREN("Tren"),
    BICICLETA("Bicicleta"),
    VEHICULO_ELECTRICO("Eléctrico");

    companion object {
        /**
         * Convierte el texto guardado en disco a un enum de forma segura.
         * Si el dato está corrupto o es de otra versión, se devuelve un valor por defecto
         * en lugar de lanzar una excepción (resiliencia ante datos anómalos).
         */
        fun desdeNombre(nombre: String?): TipoTransporte =
            entries.firstOrNull { it.name == nombre } ?: TREN
    }
}

/**
 * Modelo de datos inmutable (solo `val`) con las preferencias globales del viajero.
 * Es lo que se persiste en DataStore y sobrevive incluso al cierre total de la app.
 */
data class PreferenciaViaje(
    val nombreViajero: String = "",
    val transporte: TipoTransporte = TipoTransporte.TREN,
    val soloBajaHuella: Boolean = false
)
