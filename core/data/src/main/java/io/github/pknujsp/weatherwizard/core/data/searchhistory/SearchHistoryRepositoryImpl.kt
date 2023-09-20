package io.github.pknujsp.weatherwizard.core.data.searchhistory

import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.searchhistory.SearchHistory
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryLocalDataSource: SearchHistoryLocalDataSource
) : SearchHistoryRepository {
    override suspend fun insert(query: String): Long {
        return searchHistoryLocalDataSource.insert(query)
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