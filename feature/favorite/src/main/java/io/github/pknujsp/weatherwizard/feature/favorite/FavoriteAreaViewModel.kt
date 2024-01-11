package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.FeatureType
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
import io.github.pknujsp.weatherwizard.feature.favorite.model.LoadCurrentLocationState
import io.github.pknujsp.weatherwizard.feature.favorite.model.TargetLocationUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoriteAreaViewModel @Inject constructor(
    private val favoriteAreaRepository: FavoriteAreaListRepository,
    private val targetLocationRepository: TargetLocationRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationAddress,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val mutableTargetLocationUiState: MutableTargetLocationUiState = MutableTargetLocationUiState()
    val targetLocationUiState: TargetLocationUiState = mutableTargetLocationUiState

    val favoriteLocations = favoriteAreaRepository.getAllByFlow().distinctUntilChanged().map { list ->
        list.map {
            FavoriteArea(it.id, it.placeId, it.areaName, it.countryName)
        }
    }.flowOn(ioDispatcher).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadTargetLocation()

        getCurrentLocationUseCase.geoCodeFlow.onEach { geocode ->
            when (geocode) {
                is LocationGeoCodeState.Success -> {
                    mutableTargetLocationUiState.isLoading = true
                }

                is LocationGeoCodeState.Failure -> {
                    val featureType = when (geocode.reason) {
                        FailedReason.LOCATION_PERMISSION_DENIED -> FeatureType.LOCATION_PERMISSION
                        FailedReason.LOCATION_PROVIDER_DISABLED -> FeatureType.LOCATION_SERVICE
                        else -> null
                    }
                    mutableTargetLocationUiState.loadCurrentLocationState =
                        LoadCurrentLocationState.Failed(featureType, geocode.reason)
                    mutableTargetLocationUiState.isLoading = false
                }

                else -> {}
            }
        }.launchIn(viewModelScope)

        getCurrentLocationUseCase.geoCodeFlow.onEach {
            when (it) {
                is LocationGeoCodeState.Success -> {
                    mutableTargetLocationUiState.loadCurrentLocationState = LoadCurrentLocationState.Success(it.address)
                }

                is LocationGeoCodeState.Failure -> {
                    mutableTargetLocationUiState.loadCurrentLocationState = LoadCurrentLocationState.Failed(null, it.reason)
                }

                else -> {}
            }
            mutableTargetLocationUiState.isLoading = false
        }.launchIn(viewModelScope)
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

    fun loadCurrentLocation() {
        viewModelScope.launch {
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
                    if (size == 1 || (targetLocationUiState.locationType is LocationType.CustomLocation && targetLocationUiState.locationId == id)) {
                        targetLocationRepository.updateTargetLocation(SelectedLocationModel(LocationType.CurrentLocation))
                    }
                }
            }
            favoriteAreaRepository.deleteById(id)
        }
    }
}


private class MutableTargetLocationUiState : TargetLocationUiState {
    override var locationType: LocationType by mutableStateOf(LocationType.default)
    override var locationId: Long? by mutableStateOf(null)
    override var loadCurrentLocationState: LoadCurrentLocationState by mutableStateOf(LoadCurrentLocationState.Loading)
    override var isChanged: Boolean by mutableStateOf(false)
    override var isLoading: Boolean by mutableStateOf(true)
}