package io.github.pknujsp.weatherwizard.feature.weather.info


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
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
    val backgroundImageUrl by remember { derivedStateOf { mutableStateOf("") } }
    val weatherInfo by weatherInfoViewModel.weatherDataState.collectAsStateWithLifecycle()

    Box {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            model = ImageRequest.Builder(LocalContext.current)
                .data(backgroundImageUrl.value)
                .crossfade(250)
                .build(),
            contentDescription = stringResource(R.string.background_image),
        )

        weatherInfo.onLoading {
            CancellableLoadingScreen(stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.loading_weather_data)) {

            }
            weatherInfoViewModel.loadAllWeatherData()
        }.onSuccess {
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                item { HeadInfoScreen(weatherInfoViewModel) }
                item { CurrentWeatherScreen(weatherInfoViewModel) }
                item {
                    FlickrImageItemScreen(weatherInfoViewModel.flickrRequestParameter) {
                        backgroundImageUrl.value = it
                    }
                }
                item { HourlyForecastScreen(weatherInfoViewModel) }
                item {
                    DailyForecastScreen(weatherInfoViewModel)
                }
            }

        }.onError { throwable ->
            Text(text = throwable.message ?: "Error")
        }
    }
}

@Composable
private fun ItemSpacer(height: Dp = 12.dp) {
    Spacer(modifier = Modifier.height(height))
}