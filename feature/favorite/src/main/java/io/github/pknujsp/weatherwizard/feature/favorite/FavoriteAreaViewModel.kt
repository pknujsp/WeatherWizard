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
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.feature.favorite.model.LoadCurrentLocationState
import io.github.pknujsp.weatherwizard.feature.favorite.model.TargetLocationUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoriteAreaViewModel @Inject constructor(
    private val favoriteAreaRepository: FavoriteAreaListRepository,
    private val targetLocationRepository: TargetLocationRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val nominatimRepository: NominatimRepository,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
) : ViewModel() {

    private val mutableFavoriteLocationsUiState = MutableFavoriteListUiState()
    val favoriteLocationsUiState: FavoriteListUiState = mutableFavoriteLocationsUiState

    private val mutableTargetLocationUiState: MutableTargetLocationUiState = MutableTargetLocationUiState()
    val targetLocationUiState: TargetLocationUiState = mutableTargetLocationUiState

    init {
        loadTargetLocation()
        loadCurrentLocation()
        loadFavoriteLocations()
    }

    private fun loadTargetLocation() {
        viewModelScope.launch {
            val targetLocation = targetLocationRepository.getTargetLocation()

            mutableTargetLocationUiState.run {
                locationType = targetLocation.locationType
                locationId = if (targetLocation.locationType is LocationType.CustomLocation) targetLocation.locationId else null
            }
        }
    }

    fun loadCurrentLocation() {
        viewModelScope.launch {
            mutableTargetLocationUiState.isLoading = true
            when (val currentLocation = getCurrentLocationUseCase()) {
                is CurrentLocationResultState.Success -> {
                    withContext(ioDispatcher) {
                        val address = nominatimRepository.reverseGeoCode(currentLocation.latitude, currentLocation.longitude)
                        address.onSuccess {
                            mutableTargetLocationUiState.loadCurrentLocationState = LoadCurrentLocationState.Success(it.simpleDisplayName)
                        }.onFailure {
                            mutableTargetLocationUiState.loadCurrentLocationState =
                                LoadCurrentLocationState.Failed(null, FailedReason.REVERSE_GEOCODE_ERROR)
                        }

                        mutableTargetLocationUiState.isLoading = false
                    }
                }

                is CurrentLocationResultState.Failure -> {
                    val featureType = when (currentLocation.reason) {
                        FailedReason.LOCATION_PERMISSION_DENIED -> FeatureType.LOCATION_PERMISSION
                        FailedReason.LOCATION_PROVIDER_DISABLED -> FeatureType.LOCATION_SERVICE
                        else -> null
                    }
                    mutableTargetLocationUiState.loadCurrentLocationState =
                        LoadCurrentLocationState.Failed(featureType, currentLocation.reason)
                    mutableTargetLocationUiState.isLoading = false
                }
            }
        }
    }

    private fun loadFavoriteLocations() {
        viewModelScope.launch {
            val favoriteLocations = favoriteAreaRepository.getAll().map {
                FavoriteArea(it.id, it.placeId, it.areaName, it.countryName)
            }
            mutableFavoriteLocationsUiState.favoriteLocations = favoriteLocations
        }
    }

    fun updateTargetLocation(newModel: SelectedLocationModel) {
        viewModelScope.launch {
            targetLocationRepository.updateTargetLocation(newModel)
            while (true) {
                delay(20)
                if (targetLocationRepository.getTargetLocation() == newModel) break
            }
            mutableTargetLocationUiState.isChanged = true
        }
    }
}


private class MutableFavoriteListUiState : FavoriteListUiState {
    override var favoriteLocations: List<FavoriteArea>? by mutableStateOf(null)
}

private class MutableTargetLocationUiState : TargetLocationUiState {
    override var locationType: LocationType by mutableStateOf(LocationType.default)
    override var locationId: Long? by mutableStateOf(null)
    override var loadCurrentLocationState: LoadCurrentLocationState by mutableStateOf(LoadCurrentLocationState.Loading)
    override var isChanged: Boolean by mutableStateOf(false)
    override var isLoading: Boolean by mutableStateOf(true)
}