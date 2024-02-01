package io.github.pknujsp.everyweather.feature.weather.info


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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.common.FailedReason
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.feature.permoptimize.feature.FailedScreen
import io.github.pknujsp.everyweather.feature.permoptimize.feature.FeatureStateScreen
import io.github.pknujsp.everyweather.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.everyweather.feature.weather.comparison.dailyforecast.CompareDailyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.comparison.hourlyforecast.CompareHourlyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.detail.DetailDailyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.info.geocode.TargetLocationViewModel
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.detail.DetailHourlyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.route.NestedWeatherRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherInfoScreen(
    openDrawer: () -> Unit,
    viewModel: WeatherInfoViewModel = hiltViewModel(),
    targetLocationViewModel: TargetLocationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mainState = rememberWeatherMainState({ uiState })
    val topAppBarUiState = targetLocationViewModel.topAppBarUiState
    val isLoading = viewModel.isLoading
    val targetLocation by viewModel.targetLocation.collectAsStateWithLifecycle(initialValue = null)

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
    LaunchedEffect(mainState.networkState.isNetworkAvailable) {
        if (mainState.networkState.isNetworkAvailable && !viewModel.initialJob.isActive) {
            viewModel.initialJob.start()
        } else if (!mainState.networkState.isNetworkAvailable) {
            viewModel.onUnavailableFeature(FeatureType.NETWORK)
        }
    }

    when (mainState.nestedRoutes.value) {
        is NestedWeatherRoutes.Main -> {
            when (uiState) {
                is WeatherContentUiState.Success -> {
                    WeatherContentScreen(mainState.scrollState, mainState.scrollBehavior, navigate = {
                        if (it.isDependOnNetwork && mainState.networkState.isNetworkAvailable) {
                            mainState.navigate(it)
                        } else if (!it.isDependOnNetwork) {
                            mainState.navigate(it)
                        } else {
                            viewModel.onUnavailableFeature(FeatureType.NETWORK)
                        }

                    }, reload = {
                        mainState.refresh()
                    }, updateWeatherDataProvider = {
                        viewModel.updateWeatherDataProvider(it)
                    }, uiState as WeatherContentUiState.Success, openDrawer, topAppBarUiState)
                }

                is WeatherContentUiState.Error -> {
                    TopAppBarScreen(openDrawer) {
                        ErrorScreen(statefulFeature = (uiState as WeatherContentUiState.Error).state, reload = {
                            mainState.refresh()
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
private fun ErrorScreen(statefulFeature: StatefulFeature, reload: () -> Unit) {
    if (statefulFeature is FeatureType) {
        FeatureStateScreen(featureType = statefulFeature) {
            reload()
        }
    } else if (statefulFeature is FailedReason) {
        FailedScreen(R.string.title_failed_to_load_weather_data, statefulFeature.message, R.string.reload) {
            reload()
        }
    }
}