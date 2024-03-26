package io.github.pknujsp.everyweather.core.ui.route

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.everyweather.core.ui.NewGraph

@Composable
fun SingleGraph(
    drawInfo: DrawInfo,
    linePoint: NewGraph.LinePoint,
    text: String,
    modifier: Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier) {
        val textLayoutResult = textMeasurer.measure(text, drawInfo.textStyle)
        val amountY = (size.height - linePoint.drawingBoxHeight) / 2

        linePoint.run {
            if (leftY != -1f) {
                drawLine(
                    start = Offset(0f, leftY + amountY),
                    end = Offset(center.x, centerY + amountY),
                    color = drawInfo.lineColor,
                    strokeWidth = drawInfo.lineThickness,
                )
            }
            if (rightY != -1f) {
                drawLine(
                    start = Offset(center.x, centerY + amountY),
                    end = Offset(size.width, rightY + amountY),
                    color = drawInfo.lineColor,
                    strokeWidth = drawInfo.lineThickness,
                )
            }

            drawCircle(
                color = drawInfo.pointColor,
                radius = drawInfo.pointRadius,
                center = Offset(center.x, centerY + amountY),
                style = Fill,
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(center.x - textLayoutResult.size.width / 2, centerY + amountY + drawInfo.textSpace),
            )
        }
    }
}

@Composable
fun MultiGraph(
    drawInfo: List<DrawInfo>,
    linePoint: List<NewGraph.LinePoint>,
    text: List<String>,
    modifier: Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier) {
        var textLayoutResult: TextLayoutResult
        val amountY: Float = (size.height - linePoint[0].drawingBoxHeight) / 2

        linePoint.zip(drawInfo).forEachIndexed { i, (line, draw) ->
            textLayoutResult = textMeasurer.measure(text[i], draw.textStyle)
            line.run {
                if (leftY != -1f) {
                    drawLine(
                        start = Offset(0f, leftY + amountY),
                        end = Offset(center.x, centerY + amountY),
                        color = draw.lineColor,
                        strokeWidth = draw.lineThickness,
                    )
                }
                if (rightY != -1f) {
                    drawLine(
                        start = Offset(center.x, centerY + amountY),
                        end = Offset(size.width, rightY + amountY),
                        color = draw.lineColor,
                        strokeWidth = draw.lineThickness,
                    )
                }

                drawCircle(
                    color = draw.pointColor,
                    radius = draw.pointRadius,
                    center = Offset(center.x, centerY + amountY),
                    style = Fill,
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        center.x - textLayoutResult.size.width / 2,
                        amountY + centerY + (if (i != 0) -draw.textSpace - textLayoutResult.size.height else draw.textSpace),
                    ),
                )
            }
        }
    }
}

class DrawInfo(
    val lineColor: Color = Color.White,
    val pointColor: Color = Color.White,
    textColor: Color = Color.White,
    textSize: TextUnit = 13.sp,
    space: Dp = 4.dp,
    density: Density,
) {
    val lineThickness: Float = with(density) { 1.5.dp.toPx() }
    val pointRadius: Float = with(density) { 4.dp.toPx() }
    val textSpace = with(density) { space.toPx() }

    val textStyle = TextStyle(
        color = textColor,
        fontSize = textSize,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
}