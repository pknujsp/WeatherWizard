package io.github.pknujsp.weatherwizard.feature.favorite.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.core.annotation.KBindFunc
import io.github.pknujsp.weatherwizard.core.common.manager.AppNetworkManager
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepository
import io.github.pknujsp.weatherwizard.core.model.UiAction
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteAreaListEntity
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.nominatim.GeoCode
import io.github.pknujsp.weatherwizard.core.model.nominatim.GeoCodeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchAreaViewModel @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository,
    private val nominatimRepository: NominatimRepository,
    private val favoriteAreaRepository: FavoriteAreaListRepository,
    private val targetLocationRepository: TargetLocationRepository,
    val appNetworkManager: AppNetworkManager
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
        viewModelScope.launch(Dispatchers.IO) {
            _searchResult.value = UiState.Loading
            searchHistoryRepository.insert(query)
            val addedIds = favoriteAreaRepository.getAll().map {
                it.placeId
            }
            nominatimRepository.geoCode(query).map { geoCodeEntities ->
                geoCodeEntities.filterTypes().map { item ->
                    GeoCode(placeId = item.placeId,
                        displayName = item.simpleDisplayName,
                        countryCode = item.countryCode,
                        country = item.country,
                        latitude = item.latitude,
                        longitude = item.longitude,
                        isAdded = addedIds.contains(item.placeId))
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
            val newId = favoriteAreaRepository.insert(FavoriteAreaListEntity(areaName = geoCode.displayName,
                countryName = geoCode.country,
                latitude = geoCode.latitude,
                longitude = geoCode.longitude,
                placeId = geoCode.placeId))
            targetLocationRepository.updateTargetLocation(SelectedLocationModel(locationType = LocationType.CustomLocation,
                locationId = newId))
            _uiAction.value = Action.OnSelectedArea
        }
    }

    private fun List<GeoCodeEntity>.filterTypes(): List<GeoCodeEntity> =
        filter { it.osmType in osmTypeFilters }.filter { it.countryCode in countryCodeFilters }
}

@KBindFunc
sealed interface Action : UiAction {
    data object OnSelectedArea : Action

    data object Default : Action
}