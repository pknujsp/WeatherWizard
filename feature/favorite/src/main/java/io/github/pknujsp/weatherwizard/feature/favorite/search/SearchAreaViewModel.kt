package io.github.pknujsp.weatherwizard.feature.favorite.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.nominatim.GeoCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchAreaViewModel @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository,
    private val nominatimRepository: NominatimRepository
) : ViewModel() {

    private val _searchResult = MutableStateFlow<UiState<List<GeoCode>>>(UiState.Loading)
    val searchResult: StateFlow<UiState<List<GeoCode>>> = _searchResult

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryRepository.insert(query)
            nominatimRepository.geoCode(query).map {
                it.map { item ->
                    GeoCode(displayName = item.simpleDisplayName, countryCode =
                    item.countryCode, country = item.country, latitude = item.latitude, longitude = item.longitude
                    )
                }
            }.onSuccess {
                _searchResult.value = UiState.Success(it)
            }.onFailure {
                _searchResult.value = UiState.Error(it)
            }
        }
    }
}