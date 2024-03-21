package io.github.pknujsp.everyweather.core.database

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.everyweather.core.model.DBEntityState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreImpl(
    @ApplicationContext private val context: Context,
) : AppDataStore {
    private val Context.dataStore by preferencesDataStore(name = "preferences")

    override suspend fun save(
        key: String,
        value: String,
    ) {
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun save(
        key: String,
        value: Long,
    ) {
        context.dataStore.edit {
            it[longPreferencesKey(key)] = value
        }
    }

    override suspend fun save(
        key: String,
        value: Int,
    ) {
        context.dataStore.edit {
            it[intPreferencesKey(key)] = value
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

    override suspend fun readAsInt(key: String): DBEntityState<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)]?.run {
                DBEntityState.Exists(this)
            } ?: DBEntityState.NotExists
        }.first()
    }

    override suspend fun deleteLong(key: String) {
        context.dataStore.edit {
            it.remove(longPreferencesKey(key))
        }
    }

    override suspend fun deleteInt(key: String) {
        context.dataStore.edit {
            it.remove(intPreferencesKey(key))
        }
    }

    override suspend fun deleteString(key: String) {
        context.dataStore.edit {
            it.remove(stringPreferencesKey(key))
        }
    }

    override fun observeInt(key: String) =
        context.dataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)]
        }

    override fun observeLong(key: String) =
        context.dataStore.data.map { preferences ->
            preferences[longPreferencesKey(key)]
        }

    override fun observeString(key: String) =
        context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]
        }
}
