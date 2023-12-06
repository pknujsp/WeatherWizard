package io.github.pknujsp.weatherwizard.core.data

import javax.inject.Inject

class RepositoryInitializerManagerImpl @Inject constructor(
    private val weatherDataRepository: RepositoryInitializer, private val airQualityRepository: RepositoryInitializer
) : RepositoryInitializerManager {
    override suspend fun initialize() {
        weatherDataRepository.initialize()
        airQualityRepository.initialize()
    }
}

interface RepositoryInitializerManager {
    suspend fun initialize()
}