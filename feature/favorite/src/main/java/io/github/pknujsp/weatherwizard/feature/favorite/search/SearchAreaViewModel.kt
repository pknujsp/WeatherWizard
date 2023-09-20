package io.github.pknujsp.weatherwizard.feature.favorite.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchAreaViewModel @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    fun search(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.insert(query)
        }
    }
}