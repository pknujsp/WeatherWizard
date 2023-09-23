package io.github.pknujsp.weatherwizard.core.database.searchhistory

import javax.inject.Inject

class SearchHistoryLocalDataSourceImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryLocalDataSource {
    override suspend fun insert(query: String) {
        searchHistoryDao.insert(query)
    }

    override fun getAll() = searchHistoryDao.getAll()

    override suspend fun deleteAll() {
        searchHistoryDao.deleteAll()
    }

    override suspend fun deleteById(id: Long) {
        searchHistoryDao.deleteById(id)
    }
}