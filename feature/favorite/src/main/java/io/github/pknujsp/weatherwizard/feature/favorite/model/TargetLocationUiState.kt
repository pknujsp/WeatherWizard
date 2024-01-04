package io.github.pknujsp.weatherwizard.feature.favorite.model

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType


@Stable
interface TargetLocationUiState {
    val locationType: LocationType
    val locationId: Long?
    val loadCurrentLocationState: LoadCurrentLocationState
    val isChanged: Boolean
    val isLoading: Boolean
}

sealed interface LoadCurrentLocationState {
    data class Success(val addressName: String) : LoadCurrentLocationState
    data class Failed(val featureType: FeatureType?, val failedReason: FailedReason?) : LoadCurrentLocationState
    data object Loading : LoadCurrentLocationState
}