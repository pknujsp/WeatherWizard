package io.github.pknujsp.weatherwizard.core.data

import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class RepositoryInitializerManagerImpl @Inject constructor(
    private val weatherDataRepository: RepositoryInitializer,
    private val airQualityRepository: RepositoryInitializer,
    private val rainViewerRepository: RepositoryInitializer
) : RepositoryInitializerManager {
    override suspend fun initialize() {
        supervisorScope {
            launch {
                weatherDataRepository.initialize()
            }
            launch {
                airQualityRepository.initialize()
            }
            launch {
                rainViewerRepository.initialize()
            }
        }
    }
}

interface RepositoryInitializerManager {
    suspend fun initialize()
}