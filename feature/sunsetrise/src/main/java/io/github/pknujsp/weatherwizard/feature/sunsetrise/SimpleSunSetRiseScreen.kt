package io.github.pknujsp.weatherwizard.feature.sunsetrise

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import java.time.LocalDateTime
import java.time.ZoneId


@Composable
fun SimpleSunSetRiseScreen() {
    SimpleWeatherScreenBackground(cardInfo = CardInfo(title = stringResource(io.github.pknujsp.weatherwizard.core.common.R.string.sun_set_rise)) {

    })
}

@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
private fun SunSetRiseContent() {
    val boxHeight: Dp = 130.dp

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(boxHeight)) {
        val now = remember { LocalDateTime.now() }
        val times = remember {
            listOf(SunSetRise.SUN_SET to now.minusHours(3),
                SunSetRise.SUN_RISE to now.plusHours(4),
                SunSetRise.SUN_SET to now.plusHours(12))
        }
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

        /*
        val currentIcon =
            ImageBitmap.imageResource(if (times.first().first == SunSetRise.SUN_RISE) io.github.pknujsp.weatherwizard.core.common.R.drawable.day_clear else io.github.pknujsp.weatherwizard.core.common.R.drawable.night_clear)
        */
        val iconSize = with(LocalDensity.current) { 32.dp.toPx() }


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
            val secondVerticalDividerX = size.width * 0.55f
            val thirdVerticalDividerX = size.width * 0.9f

            // 현재 시각 아이콘 x
            val nowIconOffsetX = centerHorizontalLinePoint.x + now.run {
                val xRangeLength = secondVerticalDividerX - firstVerticalDividerX
                val zoneOffset = ZoneId.systemDefault().rules.getOffset(this)
                val previousTimeMinutes = times.first().second.toEpochSecond(zoneOffset) / 60
                val nextTimeMinutes = times.last().second.toEpochSecond(zoneOffset) / 60

                ((toEpochSecond(zoneOffset) / 60) - previousTimeMinutes) / (nextTimeMinutes - previousTimeMinutes) * xRangeLength
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
            path.moveTo(0f,
                centerHorizontalLinePoint.y + if (times.first().first == SunSetRise.SUN_RISE) curveMaxHeight else -curveMaxHeight)

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

            val currentIconY = path.asAndroidPath().computeBounds()

            drawCircle(Color.White, radius = 16f, center = Offset(nowIconOffsetX, currentIconY))
            //drawImage(image = currentIcon, topLeft = Offset(nowIconOffsetX - iconSize / 2f, currentIconY - iconSize / 2f))
        }
    }
}

private class CurveInfo(
    val point1: Offset,
    val point2: Offset,
    val point3: Offset,
)