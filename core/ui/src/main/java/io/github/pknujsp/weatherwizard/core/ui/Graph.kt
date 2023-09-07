package io.github.pknujsp.weatherwizard.core.ui

import android.content.res.Resources
import android.graphics.PointF
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times


private const val VERTICAL_SPACE = 12f
private const val TEXT_SPACE = 4f

@Composable
private fun Graph(graphData: GraphData, drawInfos: List<DrawInfo>, modifier: Modifier = Modifier, columnWidth: Dp) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier
        .then(modifier)
        .width(graphData.values.first().size * columnWidth)) {
        val (linePathList, linePoints) = graphData.createGraph(size.height)
        for (i in linePathList.indices) {
            drawPath(path = linePathList[i], color = drawInfos[i].lineColor, style = Stroke(drawInfos[i].lineThickness))
            drawPointsOnLine(linePoints[i], drawInfos[i])
            drawValueTexts(linePoints[i].zip(graphData.values[i].map { it.text }), drawInfos[i], textMeasurer)
        }
    }
}

@Composable
fun SingleGraph(graphData: GraphData, modifier: Modifier = Modifier, columnWidth: Dp) {
    Graph(graphData, listOf(DrawInfo()), modifier, columnWidth)
}

@Composable
fun MultiGraph(graphData: GraphData, modifier: Modifier = Modifier, columnWidth: Dp) {
    Graph(graphData,
        listOf(
            DrawInfo(pointColor = Color.Red),
            DrawInfo(pointColor = Color.Blue),
        ),
        modifier, columnWidth)
}

private fun DrawScope.drawPointsOnLine(points: List<PointF>, drawInfo: DrawInfo) {
    points.forEach { pointF ->
        drawCircle(
            color = drawInfo.pointColor,
            radius = drawInfo.pointRadius,
            center = Offset(pointF.x, pointF.y),
            style = Fill,
        )
    }
}

private fun DrawScope.drawValueTexts(data: List<Pair<PointF, String>>, drawInfo: DrawInfo, textMeasurer: TextMeasurer) {
    val space = TEXT_SPACE.run {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
    }
    data.forEach {
        val textLayoutResult = textMeasurer.measure(it.second, drawInfo.textStyle)
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(it.first.x - textLayoutResult.size.width / 2, it.first.y + space),
        )
    }
}


private fun calculateYPosition(value: Float, minValue: Float, valueLength: Float) = (1f - ((value - minValue) / valueLength))


private class DrawInfo(
    val lineColor: Color = Color.White,
    val pointColor: Color = Color.White,
    textColor: Color = Color.White,
    textSize: Float = 13f,
    displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics,
) {
    val lineThickness: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics)
    val pointRadius: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, displayMetrics)

    val textStyle = TextStyle(
        color = textColor,
        fontSize = textSize.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
}

data class GraphData(
    val values: List<List<Value>>,
    val columnWidth : Dp
) {
    private val minValue: Int = values.minOf { v -> v.minOf { it.value } }
    private val maxValue: Int = values.maxOf { v -> v.maxOf { it.value } }

    fun createGraph(height: Float): Pair<List<Path>, List<List<PointF>>> {
        val valueLength = (maxValue - minValue).toFloat()
        val xAxisValueIntervalSpacePixel = columnWidth.value.run {
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
        }
        val verticalSpacePixel = VERTICAL_SPACE.run {
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
        }

        val graphYAxisHeight = height - 2 * verticalSpacePixel
        val linePathList = mutableListOf<Path>()
        val startPointX = xAxisValueIntervalSpacePixel / 2
        val linePoints = mutableListOf<List<PointF>>()

        values.forEach { values ->
            val path = Path()
            linePathList.add(path)

            var lastPoint = PointF(startPointX,
                verticalSpacePixel + calculateYPosition(values.first().value.toFloat(), minValue.toFloat(), valueLength) * graphYAxisHeight)
            path.moveTo(lastPoint.x, lastPoint.y)

            val points = mutableListOf<PointF>()
            linePoints.add(points)

            values.forEachIndexed { i, value ->
                if (i > 0) {
                    val newPoint = PointF(lastPoint.x + xAxisValueIntervalSpacePixel,
                        verticalSpacePixel + calculateYPosition(value.value.toFloat(), minValue.toFloat(), valueLength) * graphYAxisHeight)
                    val point1 = PointF(lastPoint.x + xAxisValueIntervalSpacePixel / 2, lastPoint.y)
                    val point2 = PointF(point1.x, newPoint.y)

                    path.cubicTo(point1.x, point1.y, point2.x, point2.y, newPoint.x, newPoint.y)
                    lastPoint = newPoint
                }
                points.add(lastPoint)
            }
        }

        return linePathList to linePoints
    }

    data class Value(
        val value: Int, val text: String
    )
}