package io.github.pknujsp.everyweather.core.data.onboarding

import kotlinx.coroutines.flow.Flow

interface AppInitializerRepository {
    val initialized: Flow<Boolean>
    suspend fun initialize()
}