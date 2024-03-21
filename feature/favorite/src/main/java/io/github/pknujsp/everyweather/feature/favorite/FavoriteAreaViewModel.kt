package io.github.pknujsp.everyweather.feature.favorite

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.everyweather.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.everyweather.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationAddress
import io.github.pknujsp.everyweather.core.domain.location.LocationGeoCodeState
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.favorite.FavoriteArea
import io.github.pknujsp.everyweather.feature.favorite.model.LoadCurrentLocationState
import io.github.pknujsp.everyweather.feature.favorite.model.LocationUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoriteAreaViewModel
    @Inject
    constructor(
        private val favoriteAreaRepository: FavoriteAreaListRepository,
        private val targetLocationRepository: TargetLocationRepository,
        private val getCurrentLocationUseCase: GetCurrentLocationAddress,
        @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val mutableTargetLocationUiState: MutableLocationUiState = MutableLocationUiState()
        val locationUiState: LocationUiState = mutableTargetLocationUiState

        val favoriteLocations =
            favoriteAreaRepository.getAllByFlow().map { list ->
                list.map {
                    FavoriteArea(it.id, it.placeId, it.areaName, it.countryName)
                }
            }.flowOn(ioDispatcher).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        init {
            loadTargetLocation()
            setCurrentLocationFlow()
        }

        private fun loadTargetLocation() {
            viewModelScope.launch {
                withContext(ioDispatcher) { targetLocationRepository.getCurrentTargetLocation() }.let { targetLocation ->
                    mutableTargetLocationUiState.run {
                        locationType = targetLocation.locationType
                        locationId = if (targetLocation.locationType is LocationType.CustomLocation) targetLocation.locationId else null
                    }
                }
            }
        }

        private fun setCurrentLocationFlow() {
            viewModelScope.launch {
                getCurrentLocationUseCase.geoCodeFlow.collect { geocode ->
                    when (geocode) {
                        is LocationGeoCodeState.Success -> {
                            mutableTargetLocationUiState.loadCurrentLocationState = LoadCurrentLocationState.Success(geocode.address)
                        }

                        is LocationGeoCodeState.Failure -> {
                            mutableTargetLocationUiState.loadCurrentLocationState =
                                LoadCurrentLocationState.Failed(geocode.reason)
                        }

                        else -> {}
                    }
                    mutableTargetLocationUiState.isLoading = false
                }
            }
        }

        fun loadCurrentLocation() {
            viewModelScope.launch {
                mutableTargetLocationUiState.isLoading = true
                getCurrentLocationUseCase()
            }
        }

        fun updateTargetLocation(newModel: SelectedLocationModel) {
            viewModelScope.launch {
                withContext(ioDispatcher) { targetLocationRepository.updateTargetLocation(newModel) }
                mutableTargetLocationUiState.isChanged = true
            }
        }

        fun deleteFavoriteLocation(id: Long) {
            viewModelScope.launch {
                withContext(ioDispatcher) {
                    favoriteLocations.value.run {
                        if (size == 1 || (locationUiState.locationType is LocationType.CustomLocation && locationUiState.locationId == id)) {
                            targetLocationRepository.updateTargetLocation(SelectedLocationModel(LocationType.CurrentLocation))
                        }
                    }
                }
                favoriteAreaRepository.deleteById(id)
            }
        }
    }

private class MutableLocationUiState : LocationUiState {
    override var locationType: LocationType by mutableStateOf(LocationType.default)
    override var locationId: Long? by mutableStateOf(null)
    override var loadCurrentLocationState: LoadCurrentLocationState by mutableStateOf(LoadCurrentLocationState.Loading)
    override var isChanged: Boolean by mutableStateOf(false)
    override var isLoading: Boolean by mutableStateOf(true)
}
