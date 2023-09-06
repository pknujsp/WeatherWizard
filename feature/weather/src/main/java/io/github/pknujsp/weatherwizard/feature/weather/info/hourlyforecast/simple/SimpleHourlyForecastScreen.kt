package io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.DynamicDateTime
import io.github.pknujsp.weatherwizard.core.ui.GraphData
import io.github.pknujsp.weatherwizard.core.ui.SingleGraph
import io.github.pknujsp.weatherwizard.core.ui.weather.forecast.HorizontalScrollableForecast
import io.github.pknujsp.weatherwizard.core.ui.weather.item.HourlyForecastItem
import io.github.pknujsp.weatherwizard.feature.weather.info.CardInfo
import io.github.pknujsp.weatherwizard.feature.weather.info.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel
import java.time.ZonedDateTime


private val columnWidth = 48.dp

@Composable
fun HourlyForecastScreen(weatherInfoViewModel: WeatherInfoViewModel) {
    val weatherInfo = weatherInfoViewModel.weatherInfo.collectAsStateWithLifecycle()
    val columnItemModifier = Modifier
        .width(columnWidth)
        .wrapContentHeight()
    val scrollState = rememberScrollState()

    weatherInfo.value.onLoading { }.onError { }.onSuccess {
        SimpleWeatherScreenBackground(CardInfo(title = "시간별 날씨",
            buttons = listOf(
                "비교" to { },
                "자세히" to { },
            ),
            content = {
                HorizontalScrollableForecast(scrollState) {
                    DynamicDateTime(dateTimes = it.hourlyForecast.items.map { ZonedDateTime.parse(it.dateTime.value) },
                        columnWidth = columnWidth,
                        scrollState = scrollState)
                    HourlyForecastItem(hourlyForecast = it.hourlyForecast, columnItemModifier)
                    SingleGraph(graphData = GraphData(listOf(it.hourlyForecast.items.map {
                        GraphData.Value(it.temperature.value.toInt(), it.temperature.toString())
                    }), columnWidth), modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp), columnWidth)
                }
            }))
    }
}