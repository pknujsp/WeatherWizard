package io.github.pknujsp.weatherwizard.core.data

import javax.inject.Inject

class GlobalRepositoryCacheManagerImpl @Inject constructor(
    private val weatherDataRepository: RepositoryCacheManager<*, *>,
    private val airQualityRepository: RepositoryCacheManager<*, *>,
    private val rainViewerRepository: RepositoryCacheManager<*, *>
) : GlobalRepositoryCacheManager {
    override fun startCacheCleaner() {
        weatherDataRepository.startCacheCleaner()
        airQualityRepository.startCacheCleaner()
        rainViewerRepository.startCacheCleaner()
    }

    override fun stopCacheCleaner() {
        weatherDataRepository.stopCacheCleaner()
        airQualityRepository.stopCacheCleaner()
        rainViewerRepository.stopCacheCleaner()
    }

}

interface GlobalRepositoryCacheManager {
    fun startCacheCleaner()
    fun stopCacheCleaner()
}