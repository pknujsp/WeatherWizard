package io.github.pknujsp.weatherwizard.feature.sunsetrise

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.SunSetRise
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

internal fun DrawScope.drawBaseLines(pointInfo: PointInfo, timeInfo: TimeInfo, layoutInfo: LayoutInfo): Float = pointInfo.run {
    val firstCurve = timeInfo.times[0].first.run {
        val p1 = Offset(firstVerticalDividerX, centerHorizontalLinePoint.y)

        val p2X = firstVerticalDividerX + (secondVerticalDividerX - firstVerticalDividerX) * 0.25f
        val p2Y = centerHorizontalLinePoint.y + if (this == SunSetRise.SUN_RISE) -layoutInfo.curveMaxHeight else layoutInfo.curveMaxHeight
        val p2 = Offset(p2X, p2Y)

        val p3X = firstVerticalDividerX + (secondVerticalDividerX - firstVerticalDividerX) / 2f
        CurveInfo(p1, p2, Offset(p3X, p2.y))
    }

    val secondCurve = timeInfo.times[0].first.run {
        val p1 = firstCurve.point3
        val p2 = Offset(firstVerticalDividerX + (secondVerticalDividerX - firstVerticalDividerX) * 0.75f, p1.y)
        CurveInfo(p1, p2, Offset(secondVerticalDividerX, centerHorizontalLinePoint.y))
    }

    val thirdCurve = timeInfo.times[1].first.run {
        val p1 = secondCurve.point3

        val p2X = secondVerticalDividerX + (thirdVerticalDividerX - secondVerticalDividerX) * 0.25f
        val p2Y = centerHorizontalLinePoint.y + if (this == SunSetRise.SUN_RISE) -layoutInfo.curveMaxHeight else layoutInfo.curveMaxHeight
        val p2 = Offset(p2X, p2Y)

        val p3X = secondVerticalDividerX + (thirdVerticalDividerX - secondVerticalDividerX) / 2f
        CurveInfo(p1, p2, Offset(p3X, p2.y))
    }

    val fourthCurve = timeInfo.times[1].first.run {
        val p1 = thirdCurve.point3
        val p2 = Offset(secondVerticalDividerX + (thirdVerticalDividerX - secondVerticalDividerX) * 0.75f, p1.y)
        CurveInfo(p1, p2, Offset(thirdVerticalDividerX, centerHorizontalLinePoint.y))
    }

    val endCurve = timeInfo.times[2].first.run {
        val p1 = fourthCurve.point3

        val p2X = (thirdVerticalDividerX + (thirdVerticalDividerX - secondVerticalDividerX) * 0.25f)
        val p2Y = centerHorizontalLinePoint.y + if (this == SunSetRise.SUN_RISE) -layoutInfo.curveMaxHeight else layoutInfo.curveMaxHeight
        val p2 = Offset(p2X, p2Y)

        val p3X = thirdVerticalDividerX + (thirdVerticalDividerX - secondVerticalDividerX) / 2f
        CurveInfo(p1, p2, Offset(p3X, p2.y))
    }

    val path = Path()
    path.moveTo(0f, curveStartY)

    // 곡선 그리기
    path.cubicTo(firstCurve.point1.x,
        firstCurve.point1.y,
        firstCurve.point2.x,
        firstCurve.point2.y,
        firstCurve.point3.x,
        firstCurve.point3.y)
    path.cubicTo(secondCurve.point1.x,
        secondCurve.point1.y,
        secondCurve.point2.x,
        secondCurve.point2.y,
        secondCurve.point3.x,
        secondCurve.point3.y)
    path.cubicTo(thirdCurve.point1.x,
        thirdCurve.point1.y,
        thirdCurve.point2.x,
        thirdCurve.point2.y,
        thirdCurve.point3.x,
        thirdCurve.point3.y)
    path.cubicTo(fourthCurve.point1.x,
        fourthCurve.point1.y,
        fourthCurve.point2.x,
        fourthCurve.point2.y,
        fourthCurve.point3.x,
        fourthCurve.point3.y)

    path.cubicTo(endCurve.point1.x, endCurve.point1.y, endCurve.point2.x, endCurve.point2.y, endCurve.point3.x, endCurve.point3.y)
    drawPath(path, color = Color.White, style = Stroke(width = 2f))

    drawLine(Color.White,
        Offset(firstVerticalDividerX, verticalDividerTop),
        Offset(firstVerticalDividerX, verticalDividerTop + verticalDividerHeight))
    drawLine(Color.White,
        Offset(secondVerticalDividerX, verticalDividerTop),
        Offset(secondVerticalDividerX, verticalDividerTop + verticalDividerHeight))
    drawLine(Color.White,
        Offset(thirdVerticalDividerX, verticalDividerTop),
        Offset(thirdVerticalDividerX, verticalDividerTop + verticalDividerHeight))

    drawLine(Color.White, Offset(centerHorizontalLinePoint.x, centerHorizontalLinePoint.y), Offset(size.width, centerHorizontalLinePoint.y))

    PathMeasure().let { pathMeasure ->
        pathMeasure.setPath(path, false)
        val step = 0.01f
        val length = pathMeasure.length
        var closestOffset: Offset = Offset(0f, 0f)
        var minDiff = Float.MAX_VALUE
        var diff: Float
        var offset: Offset

        for (i in 0..100) {
            offset = pathMeasure.getPosition(i * step * length)
            diff = abs(nowIconOffsetX - offset.x)

            if (diff < minDiff) {
                closestOffset = offset
                minDiff = diff
            } else if (diff > minDiff) {
                break
            }
        }
        closestOffset.y
    }
}


internal fun DrawScope.drawTextAndImage(
    currentIcon: ImageBitmap, pointInfo: PointInfo, layoutInfo: LayoutInfo, timeInfo: TimeInfo, closestY: Float, textMeasurer: TextMeasurer
) {
    pointInfo.run {
        var xAmount: Float
        var yAmount: Float
        val nowTextResult =
            textMeasurer.measure(text = timeInfo.nowText, style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center)).apply {
                xAmount = size.width / 2f
                yAmount = size.height / 2f
            }

        val iconBoxX = nowIconOffsetX - layoutInfo.iconSize / 2f
        val iconBoxY = closestY - (layoutInfo.iconSize + nowTextResult.size.height) / 2f

        drawImage(currentIcon, Offset(iconBoxX, iconBoxY))

        drawText(textLayoutResult = nowTextResult,
            topLeft = Offset(nowIconOffsetX - xAmount, iconBoxY + layoutInfo.iconSize),
            color = Color.White)

        drawText(textLayoutResult = textMeasurer.measure(text = createAnnotatedString(timeInfo.timeHeaders[0].title,
            timeInfo.timeHeaders[0].content), style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center)).apply {
            xAmount = size.width / 2f
            yAmount = size.height / 2f
        }, topLeft = Offset(firstVerticalDividerX - xAmount, verticalDividerTop - yAmount), color = Color.White)

        drawText(textLayoutResult = textMeasurer.measure(text = createAnnotatedString(timeInfo.timeHeaders[1].title,
            timeInfo.timeHeaders[1].content), style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center)).apply {
            xAmount = size.width / 2f
            yAmount = size.height / 2f
        }, topLeft = Offset(secondVerticalDividerX - xAmount, verticalDividerTop - yAmount), color = Color.White)

        drawText(textLayoutResult = textMeasurer.measure(text = createAnnotatedString(timeInfo.timeHeaders[2].title,
            timeInfo.timeHeaders[2].content), style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center)).apply {
            xAmount = size.width / 2f
            yAmount = size.height / 2f
        }, topLeft = Offset(thirdVerticalDividerX - xAmount, verticalDividerTop - yAmount), color = Color.White)
    }

}

class TimeHeaderInfo(
    zonedDateTime: ZonedDateTime, setRise: SunSetRise, context: Context
) {
    val title: String = context.getString(setRise.stringRes)
    val content: String = zonedDateTime.format(dateTimeFormatter)

    private companion object {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M.d E\nHH:mm")
    }
}

private fun createAnnotatedString(title: String, content: String): AnnotatedString = listOf(AStyle(text = "${title}\n",
    span = SpanStyle(
        color = Color.White,
        fontSize = 11.sp,
    ),
    paragraph = ParagraphStyle(textAlign = TextAlign.Center)),
    AStyle(text = content,
        span = SpanStyle(
            color = Color.White,
            fontSize = 12.sp,
        ),
        paragraph = ParagraphStyle(textAlign = TextAlign.Center))).toAnnotated()


class PointInfo(
    val curveStartY: Float,
    val firstVerticalDividerX: Float,
    val secondVerticalDividerX: Float,
    val thirdVerticalDividerX: Float,
    val nowIconOffsetX: Float,
    val centerHorizontalLinePoint: Offset,
    val verticalDividerHeight: Float,
    val verticalDividerTop: Float
)

class LayoutInfo(
    val curveMaxHeight: Float, val iconSize: Float
)

private class CurveInfo(
    val point1: Offset,
    val point2: Offset,
    val point3: Offset,
)