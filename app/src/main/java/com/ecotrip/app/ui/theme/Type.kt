package com.ecotrip.app.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Identidad tipográfica: serif para los titulares (carácter de "cuaderno de viaje")
 * y sans del sistema para formularios y datos (legibilidad). Los colores NO se fijan aquí:
 * heredan del tema dinámico.
 */
private val Titulares = FontFamily.Serif

val Typography = Typography(
    displaySmall = TextStyle(
        fontFamily = Titulares, fontWeight = FontWeight.Bold,
        fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Titulares, fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp, lineHeight = 34.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Titulares, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 28.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp
    )
)

// Formas con asimetría deliberada: esquinas muy redondas arriba-izquierda, más sobrias abajo-derecha.
val EcoTripShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(topStart = 32.dp, topEnd = 12.dp, bottomEnd = 32.dp, bottomStart = 12.dp),
    extraLarge = RoundedCornerShape(CornerSize(36.dp))
)
