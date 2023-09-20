package io.github.pknujsp.weatherwizard.core.database.searchhistory

import kotlinx.coroutines.flow.Flow

interface SearchHistoryLocalDataSource {

    suspend fun insert(query: String): Long

    fun getAll(): Flow<List<SearchHistoryDto>>

    suspend fun deleteAll()

    suspend fun deleteById(id: Long)

}