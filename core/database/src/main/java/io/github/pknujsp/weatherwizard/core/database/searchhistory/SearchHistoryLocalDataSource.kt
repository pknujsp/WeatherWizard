package io.github.pknujsp.weatherwizard.core.database.searchhistory

interface SearchHistoryLocalDataSource {

    suspend fun insert(query: String): Long

    suspend fun getAll(): List<SearchHistoryDto>

    suspend fun deleteAll()

    suspend fun deleteById(id: Long)

}