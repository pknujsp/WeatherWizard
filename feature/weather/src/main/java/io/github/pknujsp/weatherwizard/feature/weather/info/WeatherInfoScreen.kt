package io.github.pknujsp.weatherwizard.feature.weather.info


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.weatherwizard.feature.airquality.AirQualityScreen
import io.github.pknujsp.weatherwizard.feature.flickr.FlickrImageItemScreen
import io.github.pknujsp.weatherwizard.feature.map.SimpleMapScreen
import io.github.pknujsp.weatherwizard.feature.sunsetrise.SimpleSunSetRiseScreen
import io.github.pknujsp.weatherwizard.feature.weather.R
import io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple.CurrentWeatherScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.SimpleDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.headinfo.HeadInfoScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple.HourlyForecastScreen

@Composable
fun WeatherInfoScreen(args: () -> RequestWeatherDataArgs) {
    Box {
        val weatherInfoViewModel: WeatherInfoViewModel = hiltViewModel()
        val backgroundImageUrl by remember { derivedStateOf { mutableStateOf("") } }
        val weatherInfo by weatherInfoViewModel.weatherDataState.collectAsStateWithLifecycle()

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            model = ImageRequest.Builder(LocalContext.current)
                .data(backgroundImageUrl.value)
                .crossfade(200)
                .build(),
            contentDescription = stringResource(R.string.background_image),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
        ) {
            HeadInfoScreen(weatherInfoViewModel)
            CurrentWeatherScreen(weatherInfoViewModel)
            FlickrImageItemScreen({ weatherInfoViewModel.flickrRequestParameter }) {
                backgroundImageUrl.value = it
            }
            HourlyForecastScreen(weatherInfoViewModel)
            SimpleDailyForecastScreen(weatherInfoViewModel)
            SimpleMapScreen { weatherInfoViewModel.requestArgs }
            AirQualityScreen { args() }
            SimpleSunSetRiseScreen()
            Spacer(modifier = Modifier.height(62.dp))
        }

        weatherInfo.onLoading {
            CancellableLoadingScreen(stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.loading_weather_data)) {
            }
            weatherInfoViewModel.loadAllWeatherData(args())
        }
    }


}

@Composable
private fun ItemSpacer(height: Dp = 12.dp) {
    Spacer(modifier = Modifier.height(height))
}