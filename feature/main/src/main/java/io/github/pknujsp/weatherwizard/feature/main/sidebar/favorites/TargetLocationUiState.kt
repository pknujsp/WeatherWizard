package io.github.pknujsp.weatherwizard.feature.main.sidebar.favorites

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType


@Stable
interface TargetLocationUiState {
    val locationType: LocationType
    val locationId: Long?
    val loadCurrentLocationState: LoadCurrentLocationState
    val isChanged: Pair<Boolean, Long>
    val isLoading: Boolean

    fun onChanged()
}

sealed interface LoadCurrentLocationState {
    data class Success(val addressName: String) : LoadCurrentLocationState
    data class Failed(val failedReason: FailedReason) : LoadCurrentLocationState
    data object Loading : LoadCurrentLocationState
}