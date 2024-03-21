package io.github.pknujsp.everyweather.feature.airquality

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.pknujsp.everyweather.core.common.util.AStyle
import io.github.pknujsp.everyweather.core.common.util.toAnnotated
import io.github.pknujsp.everyweather.core.model.airquality.SimpleAirQuality
import io.github.pknujsp.everyweather.core.resource.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("M.d\n")
private val dayFormatter = DateTimeFormatter.ofPattern("E")
private val barGraphTheme = BarGraphTheme.Small
private val dateTimeHeight = (barGraphTheme.dateTextStyle.fontSize.value + barGraphTheme.dayTextStyle.fontSize.value).dp

@Composable
fun BarGraph(
    forecast: List<SimpleAirQuality.DailyItem>,
    dateTime: LocalDate,
) {
    val textMeasurer = rememberTextMeasurer()
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.horizontalScroll(rememberScrollState()),
    ) {
        forecast.forEach { item ->
            Bar(BarGraphTheme.Small, item, dateTime, textMeasurer)
        }
    }
}

@Composable
private fun Bar(
    barGraphTheme: BarGraphTheme,
    item: SimpleAirQuality.DailyItem,
    today: LocalDate,
    textMeasurer: TextMeasurer,
) {
    Column(
        modifier =
            Modifier
                .width(barGraphTheme.barSize.width)
                .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val aqiStatusTextResult =
            textMeasurer.measure(
                stringResource(id = item.aqi.airQualityDescription.descriptionStringId),
                barGraphTheme.indexTextStyle,
                overflow = TextOverflow.Visible,
                maxLines = 1,
            )

        Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .graphicsLayer(clip = false)
                    .padding(horizontal = barGraphTheme.barSize.horizontalPadding)
                    .height(barGraphTheme.barSize.height),
        ) {
            val barHeight = (size.height - aqiStatusTextResult.size.height) * item.barHeightRatio
            val barTop = size.height - barHeight

            drawRoundRect(
                color = item.aqi.airQualityDescription.color,
                topLeft = Offset(0f, barTop),
                size = Size(size.width, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx()),
            )

            drawText(
                aqiStatusTextResult,
                topLeft = Offset((size.width - aqiStatusTextResult.size.width) / 2, barTop - aqiStatusTextResult.size.height),
            )
        }

        val isToday = item.dateTime.isEqual(today)
        Text(
            text =
                if (isToday) {
                    listOf(
                        AStyle(
                            stringResource(id = R.string.today),
                            paragraph = barGraphTheme.dateTextStyle.toParagraphStyle(),
                            span = barGraphTheme.dateTextStyle.toSpanStyle(),
                        ),
                    )
                } else {
                    listOf(
                        AStyle(
                            item.dateTime.format(dateFormatter),
                            paragraph = barGraphTheme.dateTextStyle.toParagraphStyle(),
                            span = barGraphTheme.dateTextStyle.toSpanStyle(),
                        ),
                        AStyle(
                            item.dateTime.format(dayFormatter),
                            paragraph = barGraphTheme.dayTextStyle.toParagraphStyle(),
                            span = barGraphTheme.dayTextStyle.toSpanStyle(),
                        ),
                    )
                }.toAnnotated(),
            overflow = TextOverflow.Visible,
            maxLines = 2,
            textAlign = TextAlign.Center,
            lineHeight = barGraphTheme.dateTextStyle.fontSize,
            modifier = Modifier.height(dateTimeHeight),
        )
    }
}
