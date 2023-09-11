package io.github.pknujsp.weatherwizard.feature.weather


import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoScreen

@Composable
fun WeatherMainScreen() {
    val mainViewModel: WeatherMainViewModel = hiltViewModel()
    val navController = rememberNavController()

    WeatherInfoScreen {
        RequestWeatherDataArgs(
            latitude = 35.236323256911774,
            longitude = 128.86341167027018,
            weatherDataProvider = WeatherDataProvider.Kma,
            requestId = 0)
    }
}