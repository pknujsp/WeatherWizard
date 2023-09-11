package io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherBackgroundPlaceHolder
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel

@Composable
fun SimpleDailyForecastScreen(weatherInfoViewModel: WeatherInfoViewModel) {
    val dailyForecast by weatherInfoViewModel.dailyForecast.collectAsStateWithLifecycle()

    dailyForecast.onLoading {
        SimpleWeatherBackgroundPlaceHolder()
    }.onError { }.onSuccess {
        SimpleWeatherScreenBackground(
            CardInfo(
                title = "일별 예보",
                buttons = listOf(
                    "비교" to { },
                    "자세히" to { },
                ),
                content = {
                    SimpleDailyForecastItem(it)
                }
            )
        )
    }
}