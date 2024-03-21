package io.github.pknujsp.everyweather.core.data.searchhistory

import io.github.pknujsp.everyweather.core.model.searchhistory.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    suspend fun insert(query: String)

    fun getAll(): Flow<List<SearchHistory>>

    suspend fun deleteAll()

    suspend fun deleteById(id: Long)
}
