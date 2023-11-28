package io.github.pknujsp.weatherwizard.feature.searchlocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.core.annotation.KBindFunc
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.model.UiAction
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.nominatim.GeoCode
import io.github.pknujsp.weatherwizard.core.model.nominatim.GeoCodeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val nominatimRepository: NominatimRepository,
) : ViewModel() {

    private val _searchResult = MutableStateFlow<UiState<List<GeoCode>>>(UiState.Loading)
    val searchResult: StateFlow<UiState<List<GeoCode>>> = _searchResult

    private val _uiAction = MutableStateFlow<Action>(Action.Default)
    val uiAction: StateFlow<Action> = _uiAction

    private val osmTypeFilters = arrayOf("node", "way", "relation")
    private val countryCodeFilters = arrayOf("kr")

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchResult.value = UiState.Loading
            nominatimRepository.geoCode(query).map { geoCodeEntities ->
                geoCodeEntities.filterTypes().map { item ->
                    GeoCode(placeId = item.placeId, displayName = item.simpleDisplayName, countryCode =
                    item.countryCode, country = item.country, latitude = item.latitude, longitude = item.longitude, isAdded = false
                    )
                }.distinctBy { item -> item.displayName }.map {
                    it.onSelected = {
                        onSelected(it)
                    }
                    it
                }
            }.onSuccess {
                _searchResult.value = UiState.Success(it)
            }.onFailure {
                _searchResult.value = UiState.Error(it)
            }
        }
    }

    private fun onSelected(geoCode: GeoCode) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiAction.value = Action.OnSelectedArea(CustomLocation(addressName = geoCode.displayName,
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
    data class OnSelectedArea(val location: CustomLocation) : Action

    data object Default : Action
}