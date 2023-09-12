package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.pknujsp.weatherwizard.core.model.airquality.dailyforecast.AirQualityDailyForecast
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground


@Composable
fun BarGraph(forecast: AirQualityDailyForecast) {
    SimpleWeatherScreenBackground(CardInfo(title = "대기질",
        buttons = listOf(
            "자세히" to { },
        ),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .scrollable(rememberScrollState(), orientation = Orientation.Horizontal),
            ) {
                forecast.simpleItems.forEach { item ->
                    Bar(BarGraphTheme.Small, item)
                }
            }
        }))
}


@Composable
private fun Bar(
    barGraphTheme: BarGraphTheme, item: AirQualityDailyForecast.SimpleDailyItem
) {
    Column(
        modifier = Modifier
            .width(barGraphTheme.barSize.width)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val textMeasurer = rememberTextMeasurer()
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = barGraphTheme.barSize.horizontalPadding)
                .height(barGraphTheme.barSize.height),
        ) {
            val indexTextResult = textMeasurer.measure(item.index.toString(),
                TextStyle(
                    fontSize = barGraphTheme.indexTextStyle.fontSize,
                    color = barGraphTheme.indexTextStyle.color,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                ))

            val barHeight = (size.height - indexTextResult.size.height) * item.barHeightRatio
            val barTop = size.height - barHeight

            drawRoundRect(
                color = item.airQualityDescription.color,
                topLeft = androidx.compose.ui.geometry.Offset(0f, barTop),
                size = androidx.compose.ui.geometry.Size(size.width, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            drawText(indexTextResult,
                topLeft = Offset((size.width / 2f) - (indexTextResult.size.width / 2f), barTop - indexTextResult.size
                    .height),
                color = barGraphTheme.indexTextStyle.color)
        }

        Text(text = item.dateTime,
            fontSize = barGraphTheme.dateTextStyle.fontSize,
            color = barGraphTheme.dateTextStyle.color,
            softWrap = true,
            lineHeight = barGraphTheme.dateTextStyle.fontSize * 1.4f,
            textAlign = TextAlign.Center)
    }

}