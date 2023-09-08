package io.github.pknujsp.weatherwizard.core.ui

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SingleGraph(
    drawInfo: DrawInfo,
    linePoint: NewGraph.LinePoint,
    text: String,
    modifier: Modifier,
    textMeasurer: TextMeasurer,
) {
    val textLayoutResult = textMeasurer.measure(text, drawInfo.textStyle)
    Canvas(modifier = modifier) {
        val amountY = (size.height - linePoint.drawingBoxHeight) / 2
        linePoint.run {
            if (!leftY.isNaN()) {
                drawLine(start = Offset(0f, leftY + amountY),
                    end = Offset(center.x, centerY + amountY),
                    color = drawInfo.lineColor,
                    strokeWidth = drawInfo
                        .lineThickness)
            }
            if (!rightY.isNaN()) {
                drawLine(start = Offset(center.x, centerY + amountY),
                    end = Offset(size.width, rightY + amountY),
                    color = drawInfo.lineColor,
                    strokeWidth =
                    drawInfo.lineThickness)
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


class DrawInfo(
    val lineColor: Color = Color.White,
    val pointColor: Color = Color.White,
    textColor: Color = Color.White,
    textSize: TextUnit = 13.sp,
    space: Dp = 4.dp,
    displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics,
) {
    val lineThickness: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, displayMetrics)
    val pointRadius: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, displayMetrics)
    val textSpace = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, space.value, displayMetrics)

    val textStyle = TextStyle(
        color = textColor,
        fontSize = textSize,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
}