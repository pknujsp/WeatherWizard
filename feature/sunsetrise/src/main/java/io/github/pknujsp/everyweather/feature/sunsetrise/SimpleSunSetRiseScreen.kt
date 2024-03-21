package io.github.pknujsp.everyweather.feature.sunsetrise

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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
import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.common.util.SunSetRise
import io.github.pknujsp.everyweather.core.common.util.toCalendar
import io.github.pknujsp.everyweather.core.ui.weather.item.CardInfo
import io.github.pknujsp.everyweather.core.ui.weather.item.SimpleWeatherScreenBackground
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val BOX_HEIGHT: Dp = 160.dp

@Stable
class SunSetRiseInfo(
    val latitude: Double,
    val longitude: Double,
    val dateTime: ZonedDateTime,
) {
    val dayNightCalculator = DayNightCalculator(latitude, longitude)
}

@Composable
fun SimpleSunSetRiseScreen(args: SunSetRiseInfo) {
    SimpleWeatherScreenBackground(
        cardInfo =
            CardInfo(title = stringResource(io.github.pknujsp.everyweather.core.resource.R.string.sun_set_rise)) {
                SunSetRiseContent(args)
            },
    )
}

@Composable
private fun SunSetRiseContent(args: SunSetRiseInfo) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(BOX_HEIGHT),
    ) {
        val context = LocalContext.current
        val times = remember(args) { TimeInfo(args.dayNightCalculator, context, args.dateTime) }

        /*  DisposableEffect(context) {
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
         */
        val boxHeightPx = with(LocalDensity.current) { BOX_HEIGHT.toPx() }

        // 수평 분할선 x, y
        val centerHorizontalLinePoint = Offset(0f, boxHeightPx * 0.6f)

        // 수직 분할선 높이, top
        val verticalDividerHeight = with(LocalDensity.current) { 60.dp.toPx() }
        val verticalDividerTop = (centerHorizontalLinePoint.y - verticalDividerHeight / 2)

        val curveStartEndHeightFromCenterHorizontalLine =
            with(LocalDensity.current) {
                16.dp.toPx()
            }
        val curveMaxHeight = (boxHeightPx - centerHorizontalLinePoint.y)
        val iconSize = with(LocalDensity.current) { 30.dp.toPx() }

        val currentIcon =
            ContextCompat.getDrawable(LocalContext.current, times.times[0].first.iconRes)!!
                .toBitmap(width = iconSize.toInt(), height = iconSize.toInt()).asImageBitmap()

        val textMeasurer = rememberTextMeasurer()

        Canvas(Modifier.fillMaxSize()) {
            // 곡선 시작/종료 지점 y
            val curveStartY =
                if (times.times.first().first == SunSetRise.SUN_SET) {
                    centerHorizontalLinePoint.y - curveStartEndHeightFromCenterHorizontalLine
                } else {
                    centerHorizontalLinePoint.y + curveStartEndHeightFromCenterHorizontalLine
                }

            // 수직 분할선 x
            val firstVerticalDividerX = size.width * 0.1f
            val secondVerticalDividerX = size.width * 0.6f
            val thirdVerticalDividerX = size.width * 0.9f

            // 현재 시각 아이콘 x
            val nowIconOffsetX =
                centerHorizontalLinePoint.x +
                    times.now.run {
                        val xRangeLength = secondVerticalDividerX - firstVerticalDividerX
                        val previousTimeMinutes = times.times.first().second.toEpochSecond() / 60
                        val nextTimeMinutes = times.times[1].second.toEpochSecond() / 60
                        val currMinutes = toEpochSecond() / 60

                        firstVerticalDividerX + ((currMinutes - previousTimeMinutes).toFloat() / (nextTimeMinutes - previousTimeMinutes)) * xRangeLength
                    }

            val pointInfo =
                PointInfo(
                    curveStartY = curveStartY,
                    firstVerticalDividerX = firstVerticalDividerX,
                    secondVerticalDividerX = secondVerticalDividerX,
                    thirdVerticalDividerX = thirdVerticalDividerX,
                    nowIconOffsetX = nowIconOffsetX,
                    centerHorizontalLinePoint = centerHorizontalLinePoint,
                    verticalDividerHeight = verticalDividerHeight,
                    verticalDividerTop = verticalDividerTop,
                )

            val layoutInfo = LayoutInfo(curveMaxHeight = curveMaxHeight, iconSize = iconSize)

            val closestY = drawBaseLines(pointInfo, times, layoutInfo)
            drawTextAndImage(currentIcon, pointInfo, layoutInfo, times, closestY, textMeasurer)
        }
    }
}

@Stable
internal class TimeInfo(
    dayNightCalculator: DayNightCalculator,
    context: Context,
    val now: ZonedDateTime,
) {
    val times: List<Pair<SunSetRise, ZonedDateTime>> = dayNightCalculator.getSunSetRiseTimes(now.toCalendar())
    val timeHeaders: List<TimeHeaderInfo> =
        times.map {
            TimeHeaderInfo(it.second, it.first, context)
        }
    val nowText: String = now.format(nowTimeFormatter)

    private companion object {
        val nowTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
