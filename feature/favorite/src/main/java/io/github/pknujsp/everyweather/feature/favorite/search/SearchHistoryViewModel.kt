package io.github.pknujsp.everyweather.feature.favorite.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.searchhistory.SearchHistoryRepository
import io.github.pknujsp.everyweather.core.model.searchhistory.SearchHistory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel
    @Inject
    constructor(
        private val searchHistoryRepository: SearchHistoryRepository,
        @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        val history: StateFlow<List<SearchHistory>> =
            flow {
                searchHistoryRepository.getAll().map {
                    it.onEach { item ->
                        item.onDeleteClicked = { delete(item.id) }
                    }
                }.collect {
                    emit(it)
                }
            }.flowOn(ioDispatcher).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        private fun delete(id: Long) {
            viewModelScope.launch {
                searchHistoryRepository.deleteById(id)
            }
        }
    }
