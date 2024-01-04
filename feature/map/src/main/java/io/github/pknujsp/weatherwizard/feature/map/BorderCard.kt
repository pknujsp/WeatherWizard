package io.github.pknujsp.weatherwizard.feature.map

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun AnimatedBorderCardPreview() {
    AnimatedBorderCard(onCardClick = {}, content = {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)) {
            Text("Hello World")
        }
    })
}

@Composable
fun AnimatedBorderCard(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    borderWidth: Dp = 2.dp,
    gradient: Brush = Brush.sweepGradient(listOf(Color.Blue, Color.White)),
    animationDuration: Int = 5_000,
    onCardClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite Color Animation")
    val degrees by infiniteTransition.animateFloat(initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart),
        label = "Infinite Colors")

    Surface(modifier = modifier
        .clip(shape)
        .clickable { onCardClick() }, shape = shape) {
        Surface(modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, borderWidth)
            .drawWithContent {
                rotate(degrees) {
                    drawCircle(
                        brush = gradient,
                        radius = size.width,
                        blendMode = BlendMode.SrcIn,
                    )
                }
                drawContent()
            }, shape = shape) {
            content()
        }
    }
}