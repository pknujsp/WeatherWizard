package io.github.pknujsp.everyweather.feature.main.sidebar.favorites

import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType


@Stable
interface TargetLocationUiState {
    val locationType: LocationType
    val locationId: Long?
    val isCurrentLocationLoading: Boolean
    val currentLocationAddress: String?
    val loadCurrentLocationFailedReason: StatefulFeature?
}