package io.github.pknujsp.weatherwizard.feature.sunsetrise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.SunSetRise
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs


@Composable
fun SimpleSunSetRiseScreen() {
    SimpleWeatherScreenBackground(cardInfo = CardInfo(title = stringResource(io.github.pknujsp.weatherwizard.core.common.R.string.sun_set_rise)) {
        SunSetRiseContent()
    })
}

@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
private fun SunSetRiseContent() {
    val boxHeight: Dp = 130.dp

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(boxHeight)) {
        val dayNightCalculator = remember { DayNightCalculator(37.5665, 126.9780) }

        var times by remember {
            mutableStateOf(dayNightCalculator.getSunSetRiseTimes(ZonedDateTime.now().toCalendar()))
        }

        val broadcastReceiver = remember {
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.action?.run {
                        if (this == Intent.ACTION_TIME_TICK) {
                            times = dayNightCalculator.getSunSetRiseTimes(ZonedDateTime.now().toCalendar())
                        }
                    }
                }
            }
        }

        LocalContext.current.registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        val boxHeightPx = with(LocalDensity.current) { boxHeight.toPx() }

        // 수평 분할선 x, y
        val centerHorizontalLinePoint = Offset(0f, boxHeightPx * 0.6f)

        // 수직 분할선 높이, top
        val verticalDividerHeight = boxHeightPx * 0.7f
        val verticalDividerTop = (boxHeightPx - verticalDividerHeight) / 2f

        val curveStartEndHeightFromCenterHorizontalLine = with(LocalDensity.current) {
            16.dp.toPx()
        }
        val curveMaxHeight = (boxHeightPx - centerHorizontalLinePoint.y) * 0.7f
        val iconSize = with(LocalDensity.current) { 30.dp.toPx() }

        val currentIcon = ContextCompat.getDrawable(LocalContext.current,
            if (times.first().first == SunSetRise.SUN_RISE) io.github.pknujsp.weatherwizard.core.common.R.drawable.day_clear
            else io.github.pknujsp.weatherwizard.core.common.R.drawable.night_clear)!!
            .toBitmap(width = iconSize.toInt(), height = iconSize.toInt()).asImageBitmap()

        val dateTimeFormatter = DateTimeFormatter.ofPattern("M.d E\nHH:mm")
        val textMeasurer = rememberTextMeasurer()

        Canvas(Modifier.fillMaxSize()) {
            // 곡선 시작/종료 지점 y
            val (curveStartY, curveEndY) = centerHorizontalLinePoint.y.run {
                if (times.first().first == SunSetRise.SUN_SET) {
                    this - curveStartEndHeightFromCenterHorizontalLine to this + curveStartEndHeightFromCenterHorizontalLine
                } else {
                    this + curveStartEndHeightFromCenterHorizontalLine to this - curveStartEndHeightFromCenterHorizontalLine
                }
            }

            // 수직 분할선 x
            val firstVerticalDividerX = size.width * 0.1f
            val secondVerticalDividerX = size.width * 0.6f
            val thirdVerticalDividerX = size.width * 0.9f

            // 현재 시각 아이콘 x
            val nowIconOffsetX = centerHorizontalLinePoint.x + ZonedDateTime.now().run {
                val xRangeLength = secondVerticalDividerX - firstVerticalDividerX
                val previousTimeMinutes = times.first().second.toEpochSecond() / 60
                val nextTimeMinutes = times[1].second.toEpochSecond() / 60
                val currMinutes = toEpochSecond() / 60

                firstVerticalDividerX + ((currMinutes - previousTimeMinutes).toFloat() / (nextTimeMinutes - previousTimeMinutes)) * xRangeLength
            }

            val firstCurve = times[0].first.run {
                val p1 = Offset(firstVerticalDividerX, centerHorizontalLinePoint.y)

                val p2X = firstVerticalDividerX + (secondVerticalDividerX - firstVerticalDividerX) * 0.25f
                val p2Y = centerHorizontalLinePoint.y + if (this == SunSetRise.SUN_RISE) -curveMaxHeight else curveMaxHeight
                val p2 = Offset(p2X, p2Y)

                val p3X = firstVerticalDividerX + (secondVerticalDividerX - firstVerticalDividerX) / 2f
                CurveInfo(p1, p2, Offset(p3X, p2.y))
            }

            val secondCurve = times[0].first.run {
                val p1 = firstCurve.point3
                val p2 = Offset(firstVerticalDividerX + (secondVerticalDividerX - firstVerticalDividerX) * 0.75f, p1.y)
                CurveInfo(p1, p2, Offset(secondVerticalDividerX, centerHorizontalLinePoint.y))
            }

            val thirdCurve = times[1].first.run {
                val p1 = secondCurve.point3

                val p2X = secondVerticalDividerX + (thirdVerticalDividerX - secondVerticalDividerX) * 0.25f
                val p2Y = centerHorizontalLinePoint.y + if (this == SunSetRise.SUN_RISE) -curveMaxHeight else curveMaxHeight
                val p2 = Offset(p2X, p2Y)

                val p3X = secondVerticalDividerX + (thirdVerticalDividerX - secondVerticalDividerX) / 2f
                CurveInfo(p1, p2, Offset(p3X, p2.y))
            }

            val fourthCurve = times[1].first.run {
                val p1 = thirdCurve.point3
                val p2 = Offset(secondVerticalDividerX + (thirdVerticalDividerX - secondVerticalDividerX) * 0.75f, p1.y)
                CurveInfo(p1, p2, Offset(thirdVerticalDividerX, centerHorizontalLinePoint.y))
            }

            val endCurve = times[2].first.run {
                val p1 = fourthCurve.point3

                val p2X = (thirdVerticalDividerX + (thirdVerticalDividerX - secondVerticalDividerX) * 0.25f)
                val p2Y = centerHorizontalLinePoint.y + if (this == SunSetRise.SUN_RISE) -curveMaxHeight else curveMaxHeight
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

            drawLine(Color.White,
                Offset(centerHorizontalLinePoint.x, centerHorizontalLinePoint.y),
                Offset(size.width, centerHorizontalLinePoint.y))

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

                drawImage(currentIcon, Offset(nowIconOffsetX - (iconSize / 2f), closestOffset.y - (iconSize * 0.6f)))

                var xAmount: Float
                var yAmount: Float

                drawText(textLayoutResult = textMeasurer.measure(text = times[0].second.format(dateTimeFormatter),
                    style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center)).apply {
                    xAmount = size.width / 2f
                    yAmount = size.height / 2f
                }, topLeft = Offset(firstVerticalDividerX - xAmount, verticalDividerTop - yAmount), color = Color.White)

                drawText(textLayoutResult = textMeasurer.measure(text = times[1].second.format(dateTimeFormatter),
                    style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center)).apply {
                    xAmount = size.width / 2f
                    yAmount = size.height / 2f
                }, topLeft = Offset(secondVerticalDividerX - xAmount, verticalDividerTop - yAmount), color = Color.White)

                drawText(textLayoutResult = textMeasurer.measure(text = times[2].second.format(dateTimeFormatter),
                    style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center)).apply {
                    xAmount = size.width / 2f
                    yAmount = size.height / 2f
                }, topLeft = Offset(thirdVerticalDividerX - xAmount, verticalDividerTop - yAmount), color = Color.White)
            }

        }
    }
}

private class CurveInfo(
    val point1: Offset,
    val point2: Offset,
    val point3: Offset,
)