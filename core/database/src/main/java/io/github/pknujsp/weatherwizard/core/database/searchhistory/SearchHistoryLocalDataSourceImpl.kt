package io.github.pknujsp.weatherwizard.core.database.searchhistory

import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEmpty

class SearchHistoryLocalDataSourceImpl(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryLocalDataSource {
    override suspend fun insert(query: String) {
        searchHistoryDao.insert(query)
    }

    override fun getAll() = searchHistoryDao.getAll().filterNotNull().onEmpty { emit(emptyList()) }

    override suspend fun deleteAll() {
        searchHistoryDao.deleteAll()
    }

    override suspend fun deleteById(id: Long) {
        searchHistoryDao.deleteById(id)
    }
}