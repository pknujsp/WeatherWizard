package io.github.pknujsp.weatherwizard.feature.weather


import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoScreen

@Composable
@Preview
fun WeatherMainScreen() {
    val mainViewModel: WeatherMainViewModel = hiltViewModel()
    val navController = rememberNavController()

    val view = LocalView.current
    LaunchedEffect(Unit) {
        (view.context as Activity).window.run {
            WindowCompat.getInsetsController(this, decorView).apply {
                isAppearanceLightStatusBars = false
            }
        }
    }

    WeatherInfoScreen(RequestWeatherDataArgs(
        latitude = 35.236323256911774,
        longitude = 128.86341167027018,
        weatherDataProvider = WeatherDataProvider.Kma))
}