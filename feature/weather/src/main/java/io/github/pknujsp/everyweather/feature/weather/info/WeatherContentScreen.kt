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
import io.github.pknujsp.everyweather.core.ui.lottie.CancellableLoadingDialog
import io.github.pknujsp.everyweather.core.ui.theme.SystemBarContentColor
import io.github.pknujsp.everyweather.feature.permoptimize.feature.FailedScreen
import io.github.pknujsp.everyweather.feature.weather.main.TopBar

@Composable
internal fun WeatherContentScreen(
    selectedLocationModel: SelectedLocationModel,
    openDrawer: () -> Unit,
    viewModel: WeatherContentViewModel = hiltViewModel(),
) {
    val currentSelectedLocationModel by rememberUpdatedState(newValue = selectedLocationModel)
    val currentOpenDrawer by rememberUpdatedState(newValue = openDrawer)
    val contentState = rememberWeatherContentState()

    val weatherContentUiState by viewModel.uiState.collectAsStateWithLifecycle(null)

    LaunchedEffect(currentSelectedLocationModel) {
        if (!viewModel.isLoadedLocation(currentSelectedLocationModel)) {
            viewModel.load(currentSelectedLocationModel)
        }
    }
    LaunchedEffect(weatherContentUiState) {
        contentState.setSystemBarColor(
            if (weatherContentUiState is WeatherContentUiState.Success) {
                SystemBarContentColor.WHITE
            } else {
                SystemBarContentColor.BLACK
            },
        )
    }

    when (weatherContentUiState) {
        is WeatherContentUiState.Success -> {
            WeatherInfoScreen(
                refresh = {
                    viewModel.load(currentSelectedLocationModel)
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
                    modifier = Modifier
                        .systemBarsPadding()
                        .fillMaxWidth(),
                )
                FailedScreen(
                    title = error.state.title,
                    alertMessage = error.state.message,
                    actionMessage = error.state.action,
                    onClick = {
                        viewModel.load(currentSelectedLocationModel)
                    },
                )
            }
        }

        else -> {}
    }

    if (viewModel.isLoading) {
        CancellableLoadingDialog(stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.loading_weather_data)) {
            viewModel.cancel()
        }
    }
}