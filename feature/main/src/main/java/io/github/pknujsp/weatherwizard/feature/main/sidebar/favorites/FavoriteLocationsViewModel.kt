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
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoriteLocationsViewModel @Inject constructor(
    private val favoriteAreaRepository: FavoriteAreaListRepository,
    private val targetLocationRepository: TargetLocationRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val nominatimRepository: NominatimRepository,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val mutableTargetLocationUiState: MutableTargetLocationUiState = MutableTargetLocationUiState()
    val targetLocationUiState: TargetLocationUiState = mutableTargetLocationUiState

    private val favoriteLocations = flow {
        favoriteAreaRepository.getAllByFlow().map { list ->
            list.take(ITEMS_LIMIT).map {
                FavoriteArea(it.id, it.placeId, it.areaName, it.countryName)
            } to (list.size > ITEMS_LIMIT)
        }.collect {
            emit(it.first)
            mutableFavoriteLocationsUiState.containMore = it.second
        }
    }.flowOn(ioDispatcher).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val mutableFavoriteLocationsUiState: MutableFavoriteLocationsUiState = MutableFavoriteLocationsUiState(favoriteLocations)
    val favoriteLocationsUiState: FavoriteLocationsUiState = mutableFavoriteLocationsUiState

    private companion object {
        const val ITEMS_LIMIT = 4
    }

    init {
        loadTargetLocation()
        loadCurrentLocation()
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

    private fun loadCurrentLocation() {
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
                                LoadCurrentLocationState.Failed(FailedReason.REVERSE_GEOCODE_ERROR)
                        }

                        mutableTargetLocationUiState.isLoading = false
                    }
                }

                is CurrentLocationResultState.Failure -> {
                    mutableTargetLocationUiState.loadCurrentLocationState = LoadCurrentLocationState.Failed(currentLocation.reason)
                    mutableTargetLocationUiState.isLoading = false
                }
            }
        }
    }


    fun updateTargetLocation(newModel: SelectedLocationModel) {
        viewModelScope.launch {
            targetLocationRepository.updateTargetLocation(newModel)
            mutableTargetLocationUiState.isChanged = true to System.currentTimeMillis()
        }
    }
}

private class MutableTargetLocationUiState : TargetLocationUiState {
    override var locationType: LocationType by mutableStateOf(LocationType.default)
    override var locationId: Long? by mutableStateOf(null)
    override var loadCurrentLocationState: LoadCurrentLocationState by mutableStateOf(LoadCurrentLocationState.Loading)
    override var isChanged: Pair<Boolean, Long> by mutableStateOf(false to 0)
    override var isLoading: Boolean by mutableStateOf(true)

    override fun onChanged() {
        isChanged = false to 0
    }
}

private class MutableFavoriteLocationsUiState(
    override val favoriteAreas: StateFlow<List<FavoriteArea>>
) : FavoriteLocationsUiState {
    override var containMore by mutableStateOf(false)
}