package io.github.pknujsp.weatherwizard.feature.weather.info


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple.CurrentWeatherScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.DailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.headinfo.HeadInfoScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple.HourlyForecastScreen

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun WeatherInfoScreen() {
    val weatherInfoViewModel: WeatherInfoViewModel = hiltViewModel()
    val weatherInfo = weatherInfoViewModel.weatherInfo.collectAsState()
    weatherInfoViewModel.loadAllWeatherData()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            weatherInfo.value.onLoading {
                CancellableLoadingScreen("날씨 정보를 불러오는 중") {

                }
            }.onSuccess {
                HeadInfoScreen(weatherInfoViewModel)
                ItemSpacer(60.dp)
                CurrentWeatherScreen(weatherInfoViewModel)
                ItemSpacer()
                HourlyForecastScreen(weatherInfoViewModel)
                ItemSpacer()
                DailyForecastScreen(weatherInfoViewModel)
                ItemSpacer()
            }.onError { throwable ->
                Text(text = throwable.message ?: "Error")
            }
        }
    }
}

@Composable
private fun ItemSpacer(height: Dp = 12.dp) {
    Spacer(modifier = Modifier.height(height))
}