package io.github.pknujsp.weatherwizard.core.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlaceHolder(modifier: Modifier, contentAlignment: Alignment = Alignment.TopStart, content: @Composable (BoxScope.() -> Unit)? = null) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(shimmerBrush(show = true)),
    ) {
        content?.invoke(this)
    }
}

val shimmerColors = listOf(
    Color.LightGray.copy(alpha = 0.6f),
    Color.LightGray.copy(alpha = 0.2f),
    Color.LightGray.copy(alpha = 0.6f),
)

@Composable
private fun shimmerBrush(show: Boolean): Brush = if (show) {
    val transition = rememberInfiniteTransition(label = "")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(700), repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
} else {
    Brush.linearGradient(
        colors = listOf(Color.Transparent, Color.Transparent),
        start = Offset.Zero,
        end = Offset.Zero
    )
}