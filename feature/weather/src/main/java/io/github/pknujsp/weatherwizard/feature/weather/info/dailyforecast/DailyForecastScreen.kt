package io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast

import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.weather.forecast.HorizontalScrollableForecast
import io.github.pknujsp.weatherwizard.feature.weather.info.CardInfo
import io.github.pknujsp.weatherwizard.feature.weather.info.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel

@Composable
fun DailyForecastScreen(weatherInfoViewModel: WeatherInfoViewModel) {
    val dailyForecast = weatherInfoViewModel.dailyForecast.collectAsStateWithLifecycle()

    dailyForecast.value.onLoading { }.onError { }.onSuccess {
        SimpleWeatherScreenBackground(
            CardInfo(
                title = "일별 예보",
                buttons = listOf(
                    "비교" to { },
                    "자세히" to { },
                ),
                content = {

                }
            )
        )
    }
}