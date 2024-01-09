package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.airquality.SimpleAirQuality
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("M.d\n")
private val dayFormatter = DateTimeFormatter.ofPattern("E")

@Composable
fun BarGraph(forecast: List<SimpleAirQuality.DailyItem>, dateTime: LocalDate) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .height(BarGraphTheme.SMALL.barSize.height)
            .graphicsLayer(clip = false)
            .horizontalScroll(rememberScrollState()),
    ) {
        forecast.forEach { item ->
            Bar(BarGraphTheme.SMALL, item, dateTime)
        }
    }

}


@Composable
private fun Bar(
    barGraphTheme: BarGraphTheme, item: SimpleAirQuality.DailyItem, today: LocalDate
) {
    Layout(modifier = Modifier
        .width(barGraphTheme.barSize.width)
        .fillMaxHeight(), measurePolicy = {
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)) {

            Text(text = stringResource(item.aqi.airQualityDescription.descriptionStringId),
                style = barGraphTheme.indexTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                textAlign = TextAlign.Center)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = barGraphTheme.barSize.horizontalPadding)
                    .height(barGraphTheme.barSize.height),
            ) {
                val barHeight = (size.height - indexTextResult.size.height) * item.barHeightRatio
                val barTop = size.height - barHeight

                drawRoundRect(color = item.aqi.airQualityDescription.color,
                    topLeft = Offset(0f, barTop),
                    size = Size(size.width, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx()))
            }

            val isToday = item.dateTime.isEqual(today)
            if (isToday) {
                Text(text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.today),
                    style = barGraphTheme.dateTextStyle,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Visible,
                    textAlign = TextAlign.Center)
            } else {
                val date: String = item.dateTime.format(dateFormatter)
                val day: String = item.dateTime.format(dayFormatter)

                Text(text = listOf(
                    AStyle(date, span = barGraphTheme.dateTextStyle.toSpanStyle()),
                    AStyle(day, span = barGraphTheme.dayTextStyle.toSpanStyle()),
                ).toAnnotated(), minLines = 2, maxLines = 2, overflow = TextOverflow.Visible, textAlign = TextAlign.Center)
            }
        }
    }) {

    }

}