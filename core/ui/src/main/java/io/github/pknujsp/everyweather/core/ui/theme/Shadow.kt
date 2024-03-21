package io.github.pknujsp.everyweather.core.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

@Stable
fun shadowBox(direction: ShadowDirection = ShadowDirection.DOWN): Brush =
    Brush.linearGradient(colorStops = direction.colorStops, start = direction.start, end = direction.end, tileMode = TileMode.Clamp)

enum class ShadowDirection(
    val colorStops: Array<Pair<Float, Color>>,
    val start: Offset,
    val end: Offset,
) {
    UP(
        colorStops =
            arrayOf(
                0.0f to Color.Black.copy(alpha = 0.6f),
                1.0f to Color.Transparent,
            ),
        end = Offset(0.0f, 0f),
        start = Offset(0.0f, Float.POSITIVE_INFINITY),
    ),
    DOWN(
        colorStops =
            arrayOf(
                0.0f to Color.Black.copy(alpha = 0.6f),
                1.0f to Color.Transparent,
            ),
        start = Offset(0.0f, 0f),
        end = Offset(0.0f, Float.POSITIVE_INFINITY),
    ),
}
