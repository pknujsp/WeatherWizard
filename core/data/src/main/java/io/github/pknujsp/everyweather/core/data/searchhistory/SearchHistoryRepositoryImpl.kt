package io.github.pknujsp.everyweather.core.data.searchhistory

import io.github.pknujsp.everyweather.core.database.searchhistory.SearchHistoryLocalDataSource
import io.github.pknujsp.everyweather.core.model.searchhistory.SearchHistory
import kotlinx.coroutines.flow.map

class SearchHistoryRepositoryImpl(
    private val searchHistoryLocalDataSource: SearchHistoryLocalDataSource
) : SearchHistoryRepository {
    override suspend fun insert(query: String) {
        searchHistoryLocalDataSource.insert(query)
    }

    override fun getAll() = searchHistoryLocalDataSource.getAll().map {
        it.map { searchHistoryDto ->
            SearchHistory(id = searchHistoryDto.id, query = searchHistoryDto.query)
        }
    }

    override suspend fun deleteAll() {
        searchHistoryLocalDataSource.deleteAll()
    }

    override suspend fun deleteById(id: Long) {
        searchHistoryLocalDataSource.deleteById(id)
    }
}