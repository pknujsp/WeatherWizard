package io.github.pknujsp.weatherwizard.feature.main.sidebar.favorites

import android.util.Log
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
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteLocationsViewModel @Inject constructor(
    private val targetLocationRepository: TargetLocationRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
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
        Log.d("FavoriteLocationsViewModel", "TargetLocation 흐름: $targetLocation")
        mutableTargetLocationUiState.run {
            locationType = targetLocation.locationType
            locationId = if (targetLocation.locationType is LocationType.CustomLocation) targetLocation.locationId else null
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val currentLocation = getCurrentLocationUseCase.currentLocationFlow.onEach {
        onLoadCurrentLocation(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val mutableFavoriteLocationsUiState: MutableFavoriteLocationsUiState = MutableFavoriteLocationsUiState(favoriteLocations)
    val favoriteLocationsUiState: FavoriteLocationsUiState = mutableFavoriteLocationsUiState

    private companion object {
        const val ITEMS_LIMIT = 4
    }

    init {
        viewModelScope.launch {
            getCurrentLocationUseCase.getCurrentLocationWithAddress()
        }
    }

    private fun onLoadCurrentLocation(currentLocationResultState: CurrentLocationResultState) {
        when (currentLocationResultState) {
            is CurrentLocationResultState.SuccessWithAddress -> {
                mutableTargetLocationUiState.currentLocationAddress = currentLocationResultState.address
                mutableTargetLocationUiState.isCurrentLocationLoading = false
            }

            is CurrentLocationResultState.Failure -> {
                mutableTargetLocationUiState.loadCurrentLocationFailedReason = currentLocationResultState.reason
                mutableTargetLocationUiState.isCurrentLocationLoading = false
            }

            else -> {}
        }
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