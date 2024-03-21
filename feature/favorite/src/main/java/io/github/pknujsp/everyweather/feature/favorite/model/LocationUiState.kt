package io.github.pknujsp.everyweather.feature.favorite.model

import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType

@Stable
interface LocationUiState {
    val locationType: LocationType
    val locationId: Long?
    val loadCurrentLocationState: LoadCurrentLocationState
    val isChanged: Boolean
    val isLoading: Boolean
}

sealed interface LoadCurrentLocationState {
    data class Success(val addressName: String) : LoadCurrentLocationState

    data class Failed(val statefulFeature: StatefulFeature) : LoadCurrentLocationState

    data object Loading : LoadCurrentLocationState
}
