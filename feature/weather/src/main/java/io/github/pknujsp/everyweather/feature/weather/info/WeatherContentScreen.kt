package io.github.pknujsp.everyweather.feature.weather.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.everyweather.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.everyweather.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.everyweather.core.ui.theme.SystemBarContentColor
import io.github.pknujsp.everyweather.feature.permoptimize.feature.FailedScreen
import io.github.pknujsp.everyweather.feature.weather.comparison.dailyforecast.CompareDailyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.comparison.hourlyforecast.CompareHourlyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.detail.DetailDailyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.detail.DetailHourlyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.main.TopBar
import io.github.pknujsp.everyweather.feature.weather.route.NestedWeatherRoutes

@Composable
internal fun WeatherContentScreen(
    selectedLocationModel: SelectedLocationModel,
    openDrawer: () -> Unit,
    viewModel: WeatherContentViewModel = hiltViewModel(),
) {
    val currentSelectedLocationModel by rememberUpdatedState(newValue = selectedLocationModel)
    val currentOpenDrawer by rememberUpdatedState(newValue = openDrawer)
    val contentState =
        rememberWeatherContentState {
            viewModel.load(currentSelectedLocationModel)
        }

    val weatherContentUiState by viewModel.uiState.collectAsStateWithLifecycle(null)
    LaunchedEffect(currentSelectedLocationModel) {
        viewModel.load(currentSelectedLocationModel)
    }
    LaunchedEffect(weatherContentUiState, contentState.nestedRoutes.value) {
        contentState.setSystemBarColor(
            if (weatherContentUiState is WeatherContentUiState.Success && contentState.nestedRoutes.value is NestedWeatherRoutes.Main) {
                SystemBarContentColor.WHITE
            } else {
                SystemBarContentColor.BLACK
            },
        )
    }

    when (contentState.nestedRoutes.value) {
        is NestedWeatherRoutes.Main -> {
            when (weatherContentUiState) {
                is WeatherContentUiState.Success -> {
                    WeatherInfoScreen(
                        navigate = {
                            contentState.navigate(it)
                        },
                        refresh = {
                            contentState.refresh()
                        },
                        openDrawer = openDrawer,
                        weatherContentUiState = weatherContentUiState as WeatherContentUiState.Success,
                    )
                }

                is WeatherContentUiState.Error -> {
                    val error = (weatherContentUiState as WeatherContentUiState.Error)
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopBar(
                            openDrawer = currentOpenDrawer,
                            modifier =
                                Modifier
                                    .systemBarsPadding()
                                    .fillMaxWidth(),
                        )
                        FailedScreen(
                            title = error.state.title,
                            alertMessage = error.state.message,
                            actionMessage = error.state.action,
                            onClick = {
                                contentState.refresh()
                            },
                        )
                    }
                }

                else -> {}
            }
        }

        is NestedWeatherRoutes.DetailHourlyForecast -> {
            DetailHourlyForecastScreen((weatherContentUiState as WeatherContentUiState.Success).weather.detailHourlyForecast) {
                contentState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.DetailDailyForecast -> {
            DetailDailyForecastScreen((weatherContentUiState as WeatherContentUiState.Success).weather.detailDailyForecast) {
                contentState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.ComparisonDailyForecast -> {
            CompareDailyForecastScreen((weatherContentUiState as WeatherContentUiState.Success).args) {
                contentState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.ComparisonHourlyForecast -> {
            CompareHourlyForecastScreen((weatherContentUiState as WeatherContentUiState.Success).args) {
                contentState.navigate(NestedWeatherRoutes.Main)
            }
        }
    }

    if (viewModel.isLoading) {
        CancellableLoadingScreen(stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.loading_weather_data)) {
            viewModel.cancel()
        }
    }
}
