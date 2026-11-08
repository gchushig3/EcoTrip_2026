package com.ecotrip.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Paleta "bosque" de respaldo
private val EsquemaClaroRespaldo = lightColorScheme(
    primary = Color(0xFF2E6B3F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB2F1BC),
    onPrimaryContainer = Color(0xFF00210C),
    secondary = Color(0xFF51634F),
    secondaryContainer = Color(0xFFD4E8D0),
    onSecondaryContainer = Color(0xFF0F1F10),
    tertiaryContainer = Color(0xFFBCEBEE),
    onTertiaryContainer = Color(0xFF002022),
    background = Color(0xFFF7FBF3),
    surface = Color(0xFFF7FBF3),
    surfaceContainer = Color(0xFFEBEFE7),
    surfaceContainerHigh = Color(0xFFE5E9E1)
)

private val EsquemaOscuroRespaldo = darkColorScheme(
    primary = Color(0xFF97D5A2),
    onPrimary = Color(0xFF00391A),
    primaryContainer = Color(0xFF12512A),
    onPrimaryContainer = Color(0xFFB2F1BC),
    secondary = Color(0xFFB8CCB5),
    secondaryContainer = Color(0xFF3A4B39),
    onSecondaryContainer = Color(0xFFD4E8D0),
    tertiaryContainer = Color(0xFF1F4D50),
    onTertiaryContainer = Color(0xFFBCEBEE),
    background = Color(0xFF101510),
    surface = Color(0xFF101510),
    surfaceContainer = Color(0xFF1C211B),
    surfaceContainerHigh = Color(0xFF262B25)
)

@Composable
fun EcoTripTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Solución al error de API 31: Solo usar colores dinámicos en Android 12+
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Uso de tus esquemas personalizados "bosque" como respaldo
        darkTheme -> EsquemaOscuroRespaldo
        else -> EsquemaClaroRespaldo
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        // Asegúrate de incluir las formas asimétricas que definiste en Type.kt
        shapes = EcoTripShapes,
        content = content
    )
}