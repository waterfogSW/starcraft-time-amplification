package ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ProtossGold = Color(0xFFE2C972)
val ProtossBlue = Color(0xFF00A8FF)
val ProtossDarkBlue = Color(0xFF0A1E2C)

private val ProtossColorPalette = darkColors(
    primary = ProtossBlue,
    primaryVariant = ProtossDarkBlue,
    secondary = ProtossGold,
    background = Color(0xFF0A1E2C),
    surface = Color(0xFF122334),
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

@Composable
fun ProtossTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = ProtossColorPalette,
        content = content
    )
}
