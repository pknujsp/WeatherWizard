package io.github.pknujsp.weatherwizard.feature.favorite.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.searchhistory.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _history = MutableStateFlow<UiState<List<SearchHistory>>>(UiState.Loading)
    val history: StateFlow<UiState<List<SearchHistory>>> = _history

    init {
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryRepository.getAll().map {
                it.map { item ->
                    item.onDeleteClicked = { delete(item.id) }
                    item
                }
            }.collect {
                _history.value = UiState.Success(it)
            }
        }
    }

    private fun delete(id: Long) {
        viewModelScope.launch {
            searchHistoryRepository.deleteById(id)
        }
    }
}