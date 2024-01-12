package io.github.pknujsp.weatherwizard.feature.weather.info.geocode

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider


@Stable
interface TopAppBarUiState {
    val location: LocationUiState?
}

data class LocationUiState(
    val address: String?,
    val country: String?,
)