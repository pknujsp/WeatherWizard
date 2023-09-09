package io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.DynamicDateTime
import io.github.pknujsp.weatherwizard.core.ui.weather.item.HourlyForecastItem
import io.github.pknujsp.weatherwizard.feature.weather.info.CardInfo
import io.github.pknujsp.weatherwizard.feature.weather.info.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel
import kotlinx.coroutines.DelicateCoroutinesApi


@OptIn(DelicateCoroutinesApi::class)
@Composable
fun HourlyForecastScreen(weatherInfoViewModel: WeatherInfoViewModel) {
    val hourlyForecast = weatherInfoViewModel.hourlyForecast.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    hourlyForecast.value.onLoading { }.onSuccess {
        SimpleWeatherScreenBackground(CardInfo(title = "시간별 예보",
            buttons = listOf(
                "비교" to { },
                "자세히" to { },
            ),
            content = {
                DynamicDateTime(it, lazyListState)
                HourlyForecastItem(hourlyForecast = it, lazyListState)
            }))
    }

}