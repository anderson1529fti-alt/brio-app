package com.brio.gym.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Naranja = Color(0xFFFF6B2C)
val NaranjaOscuro = Color(0xFFC94A14)
val Rojo = Color(0xFFE8263B)
val Fondo = Color(0xFF14161C)
val Superficie = Color(0xFF1E2129)
val SuperficieAlta = Color(0xFF282C36)
val Verde = Color(0xFF3DDC84)
val Dorado = Color(0xFFFFC94D)

private val esquema = darkColorScheme(
    primary = Naranja,
    onPrimary = Color.White,
    primaryContainer = NaranjaOscuro,
    onPrimaryContainer = Color.White,
    secondary = Dorado,
    onSecondary = Color(0xFF231A00),
    tertiary = Verde,
    error = Rojo,
    background = Fondo,
    onBackground = Color(0xFFF1F1F4),
    surface = Superficie,
    onSurface = Color(0xFFF1F1F4),
    surfaceVariant = SuperficieAlta,
    onSurfaceVariant = Color(0xFFB4B8C4),
    surfaceContainer = Superficie,
    surfaceContainerHigh = SuperficieAlta,
    outline = Color(0xFF4A4F5C),
)

private val tipografia = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Black, fontSize = 34.sp, letterSpacing = (-0.5).sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp),
)

@Composable
fun TemaBrio(contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = esquema, typography = tipografia, content = contenido)
}
