package io.github.pknujsp.weatherwizard.feature.weather.info


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.dialog.BottomSheet
import io.github.pknujsp.weatherwizard.core.ui.feature.FailedScreen
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.ui.lottie.NonCancellableLoadingScreen
import io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast.CompareDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast.CompareHourlyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.detail.DetailDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.detail.DetailHourlyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.route.NestedWeatherRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherInfoScreen(navController: NavController, viewModel: WeatherInfoViewModel = hiltViewModel()) {
    val mainState = rememberWeatherMainState()
    val uiState = viewModel.uiState
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(mainState.reload) {
        Log.d("WeatherInfoScreen", mainState.reload.toString())
        viewModel.initialize()
    }

    when (mainState.nestedRoutes.value) {
        is NestedWeatherRoutes.Main -> {
            when (uiState) {
                is WeatherContentUiState.Loading -> {
                    NonCancellableLoadingScreen(stringResource(id = R.string.loading_weather_data)) {}
                }

                is WeatherContentUiState.Success -> {
                    WeatherContentScreen(mainState.scrollState, scrollBehavior = mainState.scrollBehavior, navigate = {
                        coroutineScope.launch {
                            mainState.navigate(it)
                        }
                    }, reload = {
                        coroutineScope.launch {
                            mainState.reload()
                        }
                    }, updateWeatherDataProvider = {
                        coroutineScope.launch {
                            viewModel.updateWeatherDataProvider(it)
                            mainState.reload()
                        }
                    }, uiState)
                }

                is WeatherContentUiState.Error -> {
                    ErrorScreen(failedReason = uiState.message) {
                        coroutineScope.launch {
                            mainState.reload()
                        }
                    }
                }
            }
        }

        is NestedWeatherRoutes.DetailHourlyForecast -> {
            DetailHourlyForecastScreen((viewModel.uiState as WeatherContentUiState.Success).weather.detailHourlyForecast) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.DetailDailyForecast -> {
            DetailDailyForecastScreen((viewModel.uiState as WeatherContentUiState.Success).weather.detailDailyForecast) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.ComparisonDailyForecast -> {
            CompareDailyForecastScreen((viewModel.uiState as WeatherContentUiState.Success).args) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.ComparisonHourlyForecast -> {
            CompareHourlyForecastScreen((viewModel.uiState as WeatherContentUiState.Success).args) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherProviderDialog(currentProvider: WeatherProvider, onClick: (WeatherProvider?) -> Unit) {
    BottomSheet(
        onDismissRequest = {
            onClick(null)
        },
    ) {
        Column(modifier = Modifier) {
            TitleTextWithoutNavigation(title = stringResource(id = R.string.weather_provider))
            WeatherProvider.enums.forEach { weatherDataProvider ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        onClick(weatherDataProvider)
                    }
                    .fillMaxWidth()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(weatherDataProvider.icon).crossfade(false).build(),
                        contentDescription = stringResource(id = R.string.weather_provider),
                        modifier = Modifier
                            .size(34.dp)
                            .padding(start = 12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = weatherDataProvider.title),
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f))
                    RadioButton(selected = currentProvider == weatherDataProvider, onClick = {
                        onClick(weatherDataProvider)
                    }, modifier = Modifier.padding(end = 12.dp))
                }
            }
        }
    }
}

@Composable
private fun ErrorScreen(failedReason: FailedReason, reload: () -> Unit) {
    var openLocationSettings by remember { mutableStateOf(false) }

    when (failedReason) {
        FailedReason.LOCATION_PROVIDER_DISABLED -> {
            UnavailableFeatureScreen(featureType = FeatureType.LOCATION_SERVICE) {
                openLocationSettings = true
            }
            if (openLocationSettings) {
                OpenAppSettingsActivity(featureType = FeatureType.LOCATION_SERVICE) {
                    openLocationSettings = false
                    reload()
                }
            }
        }

        else -> {
            FailedScreen(R.string.title_failed_to_load_weather_data, failedReason.message, R.string.reload) {
                reload()
            }
        }
    }
}