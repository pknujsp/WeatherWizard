package io.github.pknujsp.weatherwizard.feature.weather.info


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.weatherwizard.feature.flickr.FlickrImageItemScreen
import io.github.pknujsp.weatherwizard.feature.weather.R
import io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple.CurrentWeatherScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.DailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.headinfo.HeadInfoScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple.HourlyForecastScreen

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun WeatherInfoScreen() {
    val weatherInfoViewModel: WeatherInfoViewModel = hiltViewModel()
    Log.d("WeatherInfoScreen", "loadAllWeatherData")

    val backgroundImage = rememberSaveable { mutableStateOf("") }
    val weatherInfo = weatherInfoViewModel.weatherInfo.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentScale = ContentScale.FillBounds,
            alignment = Alignment.Center,
            model = ImageRequest.Builder(LocalContext.current)
                .data(backgroundImage.value)
                .build(),
            contentDescription = stringResource(R.string.background_image),
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            weatherInfo.value.onLoading {
                weatherInfoViewModel.loadAllWeatherData()
                CancellableLoadingScreen("날씨 정보를 불러오는 중") {

                }
            }.onSuccess {
                HeadInfoScreen(weatherInfoViewModel)
                CurrentWeatherScreen(weatherInfoViewModel)
                ItemSpacer(8.dp)
                FlickrImageItemScreen(weatherInfoViewModel.flickrRequestParameter.value!!) {
                    backgroundImage.value = it
                }
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