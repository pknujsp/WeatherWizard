package io.github.pknujsp.everyweather.feature.weather.info.geocode

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationAddress
import io.github.pknujsp.everyweather.core.domain.location.LocationGeoCodeState
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.weather.TargetLocationModel
import io.github.pknujsp.everyweather.feature.weather.info.TopAppBarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TargetLocationViewModel
    @Inject
    constructor(
        getCurrentLocationUseCase: GetCurrentLocationAddress,
        private val favoriteAreaListRepository: FavoriteAreaListRepository,
    ) : ViewModel() {
        private val mutableTopAppBarUiState = MutableTopAppBarUiState()
        val topAppBarUiState: TopAppBarUiState = mutableTopAppBarUiState

        private val mutableLocationFlow = MutableStateFlow<TargetLocationModel?>(null)

        init {
            mutableLocationFlow.filterNotNull().combine(getCurrentLocationUseCase.geoCodeFlow) { argument, geoCode ->
                argument to geoCode
            }.distinctUntilChanged().onEach { (argument, geoCode) ->
                var address: String? = null
                var country: String? = null

                if (argument.locationType is LocationType.CustomLocation) {
                    val location = favoriteAreaListRepository.getById(argument.customLocationId!!)
                    location.onSuccess {
                        address = it.areaName
                        country = it.countryName
                    }
                } else if (geoCode is LocationGeoCodeState.Success) {
                    address = geoCode.address
                    country = geoCode.country
                }

                mutableTopAppBarUiState.address = address
                mutableTopAppBarUiState.country = country
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
    override var address: String? by mutableStateOf(null)
    override var country: String? by mutableStateOf(null)
}
