package io.github.pknujsp.everyweather.feature.weather.info.geocode

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationAddress
import io.github.pknujsp.everyweather.core.domain.location.LocationGeoCodeState
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.weather.TargetLocationModel
import io.github.pknujsp.everyweather.feature.weather.info.LocationUiState
import io.github.pknujsp.everyweather.feature.weather.info.TopAppBarUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TargetLocationViewModel @Inject constructor(
    getCurrentLocationUseCase: GetCurrentLocationAddress,
    private val favoriteAreaListRepository: FavoriteAreaListRepository,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val mutableTopAppBarUiState = MutableTopAppBarUiState()
    val topAppBarUiState: TopAppBarUiState = mutableTopAppBarUiState

    private val mutableLocationFlow = MutableStateFlow<TargetLocationModel?>(null)

    init {
        mutableLocationFlow.filterNotNull().combine(getCurrentLocationUseCase.geoCodeFlow) { argument, geoCode ->
            argument to geoCode
        }.map { (argument, geoCode) ->
            if (argument.locationType is LocationType.CustomLocation) {
                val location = favoriteAreaListRepository.getById(argument.customLocationId!!)
                location.fold(onSuccess = { LocationUiState(it.areaName, it.countryName) }, onFailure = { null })
            } else if (geoCode is LocationGeoCodeState.Success) {
                LocationUiState(geoCode.address, geoCode.country)
            } else {
                null
            }
        }.flowOn(dispatcher).filterNotNull().distinctUntilChanged().onEach {
            mutableTopAppBarUiState.location = it
        }.launchIn(viewModelScope)
    }

    fun setLocation(location: TargetLocationModel) {
        viewModelScope.launch {
            mutableLocationFlow.value = location
        }
    }
}

@Stable
private class MutableTopAppBarUiState : TopAppBarUiState {
    override var location: LocationUiState? by mutableStateOf(null)
}