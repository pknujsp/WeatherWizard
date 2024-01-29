package io.github.pknujsp.everyweather.feature.searchlocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.core.annotation.KBindFunc
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.nominatim.NominatimRepository
import io.github.pknujsp.everyweather.core.model.UiAction
import io.github.pknujsp.everyweather.core.model.UiState
import io.github.pknujsp.everyweather.core.model.nominatim.GeoCode
import io.github.pknujsp.everyweather.core.model.nominatim.GeoCodeEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val nominatimRepository: NominatimRepository, @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _searchResult = MutableStateFlow<UiState<List<GeoCode>>>(UiState.Loading)
    val searchResult: StateFlow<UiState<List<GeoCode>>> = _searchResult.asStateFlow()

    private val _uiAction = MutableStateFlow<Action>(Action.Default)
    val uiAction: StateFlow<Action> = _uiAction.asStateFlow()

    private companion object {
        val osmTypeFilters = arrayOf("node", "way", "relation")
        val countryCodeFilters = arrayOf("kr")
    }

    fun search(query: String) {
        viewModelScope.launch {
            _searchResult.value = UiState.Loading

            withContext(ioDispatcher) {
                nominatimRepository.geoCode(query).map { geoCodeEntities ->
                    geoCodeEntities.filterTypes().map { item ->
                        GeoCode(placeId = item.placeId,
                            displayName = item.simpleDisplayName,
                            countryCode = item.countryCode,
                            country = item.country,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            isAdded = false)
                    }.distinctBy { item -> item.displayName }.map {
                        it.onSelected = {
                            onSelected(it)
                        }
                        it
                    }
                }
            }.onSuccess {
                _searchResult.value = UiState.Success(it)
            }.onFailure {
                _searchResult.value = UiState.Error(it)
            }
        }
    }

    private fun onSelected(geoCode: GeoCode) {
        viewModelScope.launch {
            _uiAction.value = Action.OnSelectedArea(PickedLocation(addressName = geoCode.displayName,
                countryName = geoCode.country,
                latitude = geoCode.latitude,
                longitude = geoCode.longitude,
                placeId = geoCode.placeId))
        }
    }

    private fun List<GeoCodeEntity>.filterTypes(): List<GeoCodeEntity> =
        filter { it.osmType in osmTypeFilters }.filter { it.countryCode in countryCodeFilters }
}

@KBindFunc
sealed interface Action : UiAction {
    data class OnSelectedArea(val location: PickedLocation) : Action

    data object Default : Action
}