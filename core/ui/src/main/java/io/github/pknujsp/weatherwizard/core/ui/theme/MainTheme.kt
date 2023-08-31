package io.github.pknujsp.weatherwizard.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val mainColorScheme = lightColorScheme(
    primary = Color.Blue,
    onSurface = Color.Black,
    background = Color.White,
    surface = Color.White,
)


@Composable
fun MainTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = mainColorScheme) {
        content()
    }
}