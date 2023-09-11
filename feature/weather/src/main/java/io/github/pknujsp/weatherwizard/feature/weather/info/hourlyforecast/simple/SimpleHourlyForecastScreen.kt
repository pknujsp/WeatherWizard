package io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.DynamicDateTime
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.HourlyForecastItem
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherBackgroundPlaceHolder
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel


@Composable
fun HourlyForecastScreen(weatherInfoViewModel: WeatherInfoViewModel) {
    val hourlyForecast by weatherInfoViewModel.hourlyForecast.collectAsStateWithLifecycle()

    hourlyForecast.onLoading {
        SimpleWeatherBackgroundPlaceHolder()
    }.onSuccess {
        SimpleWeatherScreenBackground(CardInfo(title = "시간별 예보",
            buttons = listOf(
                "비교" to { },
                "자세히" to { },
            ),
            content = {
                val lazyListState = rememberLazyListState()

                DynamicDateTime(it, lazyListState)
                HourlyForecastItem(hourlyForecast = it, lazyListState)
            }))
    }

}