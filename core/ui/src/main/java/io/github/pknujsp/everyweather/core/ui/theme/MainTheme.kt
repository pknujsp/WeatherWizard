package io.github.pknujsp.everyweather.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val AppColorScheme = lightColorScheme(
    primary = Color(0xFF673AB7),
    onSurface = Color.Black,
    background = Color.White,
    surface = Color.White,
    primaryContainer = Color.White,
    surfaceTint = Color.White,
)

val AppShapes = Shapes(extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(10.dp)
)

val CircularIndicatorTrackColor = Color(0xFFD9D9D9)
val CircularIndicatorColor = Color.Black

@Composable
fun MainTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColorScheme,
        shapes = AppShapes) {
        content()
    }
}