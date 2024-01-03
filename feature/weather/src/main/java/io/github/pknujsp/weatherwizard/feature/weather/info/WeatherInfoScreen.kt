package io.github.pknujsp.weatherwizard.feature.weather.info


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.feature.FailedScreen
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast.CompareDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast.CompareHourlyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.detail.DetailDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.detail.DetailHourlyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.route.NestedWeatherRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherInfoScreen(navController: NavController, openDrawer: () -> Unit, viewModel: WeatherInfoViewModel = hiltViewModel()) {
    val mainState = rememberWeatherMainState()
    val uiState = viewModel.uiState
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(mainState.reload) {
        Log.d("WeatherInfoScreen", "reload")
        viewModel.initialize()
    }

    when (mainState.nestedRoutes.value) {
        is NestedWeatherRoutes.Main -> {
            when (uiState) {
                is WeatherContentUiState.Loading -> {
                    CancellableLoadingScreen(stringResource(id = R.string.loading_weather_data)) {
                        viewModel.cancelLoading()
                    }
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
                        }
                    }, updateWindowInset = {
                        coroutineScope.launch {
                            mainState.updateWindowInset(false)
                        }
                    }, uiState, openDrawer)
                }

                is WeatherContentUiState.Error -> {
                    mainState.updateWindowInset(true)
                    TopAppBarScreen(openDrawer) {
                        ErrorScreen(failedReason = uiState.message) {
                            coroutineScope.launch {
                                mainState.reload()
                            }
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
private fun TopAppBarScreen(openDrawer: () -> Unit, content: @Composable () -> Unit) {
    Column(modifier = Modifier.systemBarsPadding()) {
        TopAppBar(title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
            navigationIcon = {
                IconButton(onClick = openDrawer) {
                    Icon(Icons.Rounded.Menu, contentDescription = null)
                }
            })
        content()
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