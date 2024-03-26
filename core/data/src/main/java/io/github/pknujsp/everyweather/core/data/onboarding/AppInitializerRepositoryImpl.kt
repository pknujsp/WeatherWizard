package io.github.pknujsp.everyweather.core.data.onboarding

import io.github.pknujsp.everyweather.core.database.AppDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val INITIALIZED_KEY = "initialized"

class AppInitializerRepositoryImpl(
    private val appDataStore: AppDataStore
) : AppInitializerRepository {
    override val initialized: Flow<Boolean> = appDataStore.observeInt(INITIALIZED_KEY).map {
        if (it == null) false else it == 1
    }

    override suspend fun initialize() {
        appDataStore.save(INITIALIZED_KEY, 1)
    }

}