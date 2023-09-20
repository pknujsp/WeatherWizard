package io.github.pknujsp.weatherwizard.core.data.searchhistory

import io.github.pknujsp.weatherwizard.core.model.searchhistory.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    suspend fun insert(query: String): Long
    fun getAll(): Flow<List<SearchHistory>>
    suspend fun deleteAll()
    suspend fun deleteById(id: Long)
}