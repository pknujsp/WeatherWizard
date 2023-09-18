package io.github.pknujsp.weatherwizard.feature.weather


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun WeatherMainScreen() {
    val mainViewModel: WeatherMainViewModel = hiltViewModel()
    val navController = rememberNavController()

    WeatherInfoScreen(RequestWeatherDataArgs(
        latitude = 35.236323256911774,
        longitude = 128.86341167027018,
        weatherDataProvider = WeatherDataProvider.Kma))
}

@Composable
private fun TopBar() {

}