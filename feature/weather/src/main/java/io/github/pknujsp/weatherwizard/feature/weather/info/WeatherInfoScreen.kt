package io.github.pknujsp.weatherwizard.feature.weather.info


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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import io.github.pknujsp.weatherwizard.feature.weather.info.geocode.TargetLocationViewModel
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.detail.DetailHourlyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.route.NestedWeatherRoutes
import kotlinx.coroutines.flow.filterNotNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherInfoScreen(
    openDrawer: () -> Unit,
    viewModel: WeatherInfoViewModel = hiltViewModel(),
    targetLocationViewModel: TargetLocationViewModel = hiltViewModel()
) {
    val mainState = rememberWeatherMainState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topAppBarUiState = targetLocationViewModel.topAppBarUiState
    val isLoading = viewModel.isLoading
    val targetLocation by viewModel.targetLocation.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is WeatherContentUiState.Success) {
            mainState.expandAppBar()
        }
    }
    LaunchedEffect(targetLocation) {
        targetLocation?.run {
            targetLocationViewModel.setLocation(this)
        }
    }

    when (mainState.nestedRoutes.value) {
        is NestedWeatherRoutes.Main -> {
            when (uiState) {
                is WeatherContentUiState.Success -> {
                    WeatherContentScreen(mainState.scrollState, mainState.scrollBehavior, navigate = {
                        mainState.navigate(it)
                    }, reload = {
                        viewModel.refresh()
                    }, updateWeatherDataProvider = {
                        viewModel.updateWeatherDataProvider(it)
                    }, updateWindowInset = {
                        mainState.updateWindowInset(false)
                    }, uiState as WeatherContentUiState.Success, openDrawer, topAppBarUiState)
                }

                is WeatherContentUiState.Error -> {
                    mainState.updateWindowInset(true)
                    TopAppBarScreen(openDrawer) {
                        ErrorScreen(failedReason = (uiState as WeatherContentUiState.Error).message, reload = {
                            viewModel.refresh()
                        })
                    }
                }

                else -> {}
            }
        }

        is NestedWeatherRoutes.DetailHourlyForecast -> {
            DetailHourlyForecastScreen((uiState as WeatherContentUiState.Success).weather.detailHourlyForecast) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.DetailDailyForecast -> {
            DetailDailyForecastScreen((uiState as WeatherContentUiState.Success).weather.detailDailyForecast) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.ComparisonDailyForecast -> {
            CompareDailyForecastScreen((uiState as WeatherContentUiState.Success).args) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.ComparisonHourlyForecast -> {
            CompareHourlyForecastScreen((uiState as WeatherContentUiState.Success).args) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }
    }

    if (isLoading) {
        CancellableLoadingScreen(stringResource(id = R.string.loading_weather_data)) {
            viewModel.cancelLoading()
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