package io.github.pknujsp.weatherwizard.feature.main.sidebar.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationAddress
import io.github.pknujsp.weatherwizard.core.domain.location.LocationGeoCodeState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoriteLocationsViewModel @Inject constructor(
    private val targetLocationRepository: TargetLocationRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationAddress,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
    favoriteAreaRepository: FavoriteAreaListRepository,
) : ViewModel() {

    private val mutableTargetLocationUiState: MutableTargetLocationUiState = MutableTargetLocationUiState()
    val targetLocationUiState: TargetLocationUiState = mutableTargetLocationUiState

    private val favoriteLocations = favoriteAreaRepository.getAllByFlow().distinctUntilChanged().map { list ->
        list.take(ITEMS_LIMIT).map {
            FavoriteArea(it.id, it.placeId, it.areaName, it.countryName)
        }
    }.flowOn(ioDispatcher).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val targetLocationFlow = targetLocationRepository.targetLocation.distinctUntilChanged().onEach { targetLocation ->
        mutableTargetLocationUiState.run {
            locationType = targetLocation.locationType
            locationId = if (targetLocation.locationType is LocationType.CustomLocation) targetLocation.locationId else null
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val mutableFavoriteLocationsUiState: MutableFavoriteLocationsUiState = MutableFavoriteLocationsUiState(favoriteLocations)
    val favoriteLocationsUiState: FavoriteLocationsUiState = mutableFavoriteLocationsUiState

    private companion object {
        const val ITEMS_LIMIT = 4
    }

    init {
        if (getCurrentLocationUseCase.geoCodeFlow.value == null) {
            viewModelScope.launch {
                withContext(ioDispatcher) {
                    getCurrentLocationUseCase()
                }
            }
        }
        getCurrentLocationUseCase.geoCodeFlow.filterNotNull().onEach { geoCode ->
            onResultCurrentLocation(geoCode)
        }.launchIn(viewModelScope)
    }

    private fun onResultCurrentLocation(geoCodeState: LocationGeoCodeState) {
        when (geoCodeState) {
            is LocationGeoCodeState.Success -> {
                mutableTargetLocationUiState.currentLocationAddress = geoCodeState.address
            }

            is LocationGeoCodeState.Failure -> {
                mutableTargetLocationUiState.loadCurrentLocationFailedReason = geoCodeState.reason
            }
        }
        mutableTargetLocationUiState.isCurrentLocationLoading = false
    }


    fun updateTargetLocation(newModel: SelectedLocationModel) {
        viewModelScope.launch {
            targetLocationRepository.updateTargetLocation(newModel)
        }
    }
}

private class MutableTargetLocationUiState : TargetLocationUiState {
    override var locationType: LocationType by mutableStateOf(LocationType.default)
    override var locationId: Long? by mutableStateOf(null)
    override var isCurrentLocationLoading: Boolean by mutableStateOf(true)
    override var currentLocationAddress: String? by mutableStateOf(null)
    override var loadCurrentLocationFailedReason: FailedReason? by mutableStateOf(null)
}

private class MutableFavoriteLocationsUiState(
    override val favoriteAreas: StateFlow<List<FavoriteArea>>
) : FavoriteLocationsUiState