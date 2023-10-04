package io.github.pknujsp.weatherwizard.core.database

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.model.DBEntityState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppDataStore {
    private val Context.dataStore by preferencesDataStore(name = "preferences")

    override suspend fun save(key: String, value: String) {
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = value

        }
    }

    override suspend fun save(key: String, value: Long) {
        context.dataStore.edit {
            it[longPreferencesKey(key)] = value
        }
    }

    override suspend fun readAsString(key: String): DBEntityState<String> {
        return context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]?.run {
                DBEntityState.Exists(this)
            } ?: DBEntityState.NotExists
        }.first()
    }

    override suspend fun readAsLong(key: String): DBEntityState<Long> {
        return context.dataStore.data.map { preferences ->
            preferences[longPreferencesKey(key)]?.run {
                DBEntityState.Exists(this)
            } ?: DBEntityState.NotExists
        }.first()
    }

    override suspend fun deleteLong(key: String) {
        context.dataStore.edit {
            it.remove(longPreferencesKey(key))
        }
    }

    override suspend fun deleteString(key: String) {
        context.dataStore.edit {
            it.remove(stringPreferencesKey(key))
        }
    }

}