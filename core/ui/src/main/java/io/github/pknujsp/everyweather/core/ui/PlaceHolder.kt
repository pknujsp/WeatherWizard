package io.github.pknujsp.everyweather.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import kotlinx.serialization.json.JsonNull.content

private const val SHADOW_BRUSH_WIDTH = 500
private const val DURATION = 900
private const val ANGLE_OF_AXIS_Y = 265f

private val shimmerColors = listOf(
    Color.White.copy(alpha = 0.3f),
    Color.White.copy(alpha = 0.5f),
    Color.White.copy(alpha = 1.0f),
    Color.White.copy(alpha = 0.5f),
    Color.White.copy(alpha = 0.3f),
)

fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (DURATION + SHADOW_BRUSH_WIDTH).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = DURATION,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "Shimmer animation",
    )

    this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value - SHADOW_BRUSH_WIDTH, y = 0.0f),
            end = Offset(x = translateAnimation.value, y = ANGLE_OF_AXIS_Y),
        ),
    )
}