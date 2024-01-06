package io.github.pknujsp.weatherwizard.core.database

import io.github.pknujsp.weatherwizard.core.model.DBEntityState
import kotlinx.coroutines.flow.Flow

interface AppDataStore {

    suspend fun save(key: String, value: String)
    suspend fun save(key: String, value: Long)
    suspend fun save(key: String, value: Int)

    suspend fun readAsString(key: String): DBEntityState<String>
    suspend fun readAsLong(key: String): DBEntityState<Long>
    suspend fun readAsInt(key: String): DBEntityState<Int>

    suspend fun deleteString(key: String)
    suspend fun deleteLong(key: String)
    suspend fun deleteInt(key: String)

    fun observeString(key: String): Flow<String?>
    fun observeLong(key: String): Flow<Long?>
    fun observeInt(key: String): Flow<Int?>
}