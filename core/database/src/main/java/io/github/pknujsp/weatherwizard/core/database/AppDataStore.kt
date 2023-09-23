package io.github.pknujsp.weatherwizard.core.database

import io.github.pknujsp.weatherwizard.core.model.DBEntityState

interface AppDataStore {

    suspend fun save(key: String, value: String)
    suspend fun save(key: String, value: Long)

    suspend fun readAsString(key: String): DBEntityState<String>
    suspend fun readAsLong(key: String): DBEntityState<Long>

    suspend fun deleteString(key: String)
    suspend fun deleteLong(key: String)

}