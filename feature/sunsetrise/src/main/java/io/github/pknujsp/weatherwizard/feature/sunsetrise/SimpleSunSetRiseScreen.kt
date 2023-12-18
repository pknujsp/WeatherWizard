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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.SunSetRise
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun SimpleSunSetRiseScreen(args: RequestWeatherArguments) {
    SimpleWeatherScreenBackground(cardInfo = CardInfo(title = stringResource(io.github.pknujsp.weatherwizard.core.resource.R.string.sun_set_rise)) {
        SunSetRiseContent(args.location.latitude, args.location.longitude)
    })
}

@Composable
private fun SunSetRiseContent(latitude: Double, longitude: Double) {
    val boxHeight: Dp = 160.dp

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(boxHeight)) {
        val dayNightCalculator = remember { DayNightCalculator(latitude, longitude) }
        val context = LocalContext.current

        val times = remember {
            mutableStateOf(TimeInfo(dayNightCalculator, context))
        }

        DisposableEffect(context) {
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    context?.run {
                        times.value = TimeInfo(dayNightCalculator, this)
                    }
                }
            }
            context.registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
            onDispose {
                context.unregisterReceiver(broadcastReceiver)
            }
        }

        val boxHeightPx = with(LocalDensity.current) { boxHeight.toPx() }

        // 수평 분할선 x, y
        val centerHorizontalLinePoint = Offset(0f, boxHeightPx * 0.6f)

        // 수직 분할선 높이, top
        val verticalDividerHeight =with(LocalDensity.current) { 60.dp.toPx() }
        val verticalDividerTop = (centerHorizontalLinePoint.y - verticalDividerHeight / 2)

        val curveStartEndHeightFromCenterHorizontalLine = with(LocalDensity.current) {
            16.dp.toPx()
        }
        val curveMaxHeight = (boxHeightPx - centerHorizontalLinePoint.y)
        val iconSize = with(LocalDensity.current) { 30.dp.toPx() }

        val currentIcon = ContextCompat.getDrawable(LocalContext.current, times.value.times[0].first.iconRes)!!.toBitmap(width =
        iconSize.toInt(), height = iconSize.toInt()).asImageBitmap()

        val textMeasurer = rememberTextMeasurer()

        Canvas(Modifier.fillMaxSize()) {
            // 곡선 시작/종료 지점 y
            val curveStartY = if (times.value.times.first().first == SunSetRise.SUN_SET) {
                centerHorizontalLinePoint.y - curveStartEndHeightFromCenterHorizontalLine
            } else {
                centerHorizontalLinePoint.y + curveStartEndHeightFromCenterHorizontalLine
            }

            // 수직 분할선 x
            val firstVerticalDividerX = size.width * 0.1f
            val secondVerticalDividerX = size.width * 0.6f
            val thirdVerticalDividerX = size.width * 0.9f

            // 현재 시각 아이콘 x
            val nowIconOffsetX = centerHorizontalLinePoint.x + times.value.now.run {
                val xRangeLength = secondVerticalDividerX - firstVerticalDividerX
                val previousTimeMinutes = times.value.times.first().second.toEpochSecond() / 60
                val nextTimeMinutes = times.value.times[1].second.toEpochSecond() / 60
                val currMinutes = toEpochSecond() / 60

                firstVerticalDividerX + ((currMinutes - previousTimeMinutes).toFloat() / (nextTimeMinutes - previousTimeMinutes)) * xRangeLength
            }

            val pointInfo = PointInfo(
                curveStartY = curveStartY,
                firstVerticalDividerX = firstVerticalDividerX,
                secondVerticalDividerX = secondVerticalDividerX,
                thirdVerticalDividerX = thirdVerticalDividerX,
                nowIconOffsetX = nowIconOffsetX,
                centerHorizontalLinePoint = centerHorizontalLinePoint,
                verticalDividerHeight = verticalDividerHeight,
                verticalDividerTop = verticalDividerTop,
            )

            val layoutInfo = LayoutInfo(curveMaxHeight = curveMaxHeight,
                iconSize = iconSize)

            val closestY = drawBaseLines(pointInfo, times.value, layoutInfo)
            drawTextAndImage(currentIcon, pointInfo, layoutInfo, times.value, closestY, textMeasurer)
        }
    }
}


internal class TimeInfo(
    dayNightCalculator: DayNightCalculator, context: Context
) {
    val now: ZonedDateTime = ZonedDateTime.now()
    val times: List<Pair<SunSetRise, ZonedDateTime>> = dayNightCalculator.getSunSetRiseTimes(now.toCalendar())
    val timeHeaders: List<TimeHeaderInfo> = times.map {
        TimeHeaderInfo(it.second, it.first, context)
    }
    val nowText: String = now.format(nowTimeFormatter)

    private companion object {
        val nowTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}