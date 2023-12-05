package io.github.pknujsp.weatherwizard.feature.weather.info

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.ProcessState
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments


@Stable
interface WeatherMainUiState {
    val isGpsEnabled: Boolean
    val processState: ProcessState
    val args: RequestWeatherArguments
    val lastUpdatedTime: String
}