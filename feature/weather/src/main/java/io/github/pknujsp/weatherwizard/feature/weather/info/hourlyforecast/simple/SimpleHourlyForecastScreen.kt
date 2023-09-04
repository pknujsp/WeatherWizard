package io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.feature.weather.info.CardInfo
import io.github.pknujsp.weatherwizard.feature.weather.info.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel

@Composable
fun HourlyForecastScreen(weatherInfoViewModel: WeatherInfoViewModel) {
    val weatherInfo = weatherInfoViewModel.weatherInfo.collectAsStateWithLifecycle()

    weatherInfo.value.onLoading { }.onError { }.onSuccess {
        SimpleWeatherScreenBackground(
            CardInfo(
                title = "시간별 날씨",
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