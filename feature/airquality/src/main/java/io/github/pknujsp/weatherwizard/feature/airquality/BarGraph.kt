package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.pknujsp.weatherwizard.core.model.airquality.SimpleAirQuality
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun BarGraph(forecast: List<SimpleAirQuality.DailyItem>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(clip = false)
            .wrapContentHeight()
            .horizontalScroll(rememberScrollState()),
    ) {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("M.d\nE")
        val today = LocalDate.now()

        forecast.forEach { item ->
            Bar(BarGraphTheme.Small, item, dateTimeFormatter, today)
        }
    }

}


@Composable
private fun Bar(
    barGraphTheme: BarGraphTheme, item: SimpleAirQuality.DailyItem, dateTimeFormatter: DateTimeFormatter, today: LocalDate
) {
    Column(
        modifier = Modifier
            .width(barGraphTheme.barSize.width)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val indexTextResult = rememberTextMeasurer().measure(text = stringResource(item.aqi.airQualityDescription.descriptionStringId),
            maxLines = 1,
            style = TextStyle(
                fontSize = barGraphTheme.indexTextStyle.fontSize,
                color = barGraphTheme.indexTextStyle.color,
                textAlign = TextAlign.Center,
            ))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = barGraphTheme.barSize.horizontalPadding)
                .height(barGraphTheme.barSize.height),
        ) {
            val barHeight = (size.height - indexTextResult.size.height) * item.barHeightRatio
            val barTop = size.height - barHeight

            drawRoundRect(
                color = item.aqi.airQualityDescription.color,
                topLeft = androidx.compose.ui.geometry.Offset(0f, barTop),
                size = androidx.compose.ui.geometry.Size(size.width, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            drawText(indexTextResult,
                topLeft = Offset((size.width / 2f) - (indexTextResult.size.width / 2f), barTop - indexTextResult.size
                    .height),
                color = barGraphTheme.indexTextStyle.color)
        }

        val isToday = item.dateTime.isEqual(today)
        Text(text = if (isToday) stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.today)
        else item.dateTime.format(dateTimeFormatter),
            fontSize = barGraphTheme.dateTextStyle.fontSize,
            color = barGraphTheme.dateTextStyle.color,
            lineHeight = barGraphTheme.dateTextStyle.fontSize,
            maxLines = 2,
            overflow = TextOverflow.Visible,
            textDecoration = if (isToday)  TextDecoration.Underline else null,
            textAlign = TextAlign.Center)
    }

}