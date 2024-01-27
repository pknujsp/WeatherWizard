package io.github.pknujsp.everyweather.feature.weather.info.geocode

import androidx.compose.runtime.Stable


@Stable
interface TopAppBarUiState {
    val location: LocationUiState?
}

data class LocationUiState(
    val address: String?,
    val country: String?,
)