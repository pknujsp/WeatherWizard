package io.github.pknujsp.everyweather.core.data

internal class GlobalRepositoryCacheManagerImpl(
    private val weatherDataRepository: RepositoryCacheManager<*, *>,
    private val airQualityRepository: RepositoryCacheManager<*, *>,
    private val rainViewerRepository: RepositoryCacheManager<*, *>,
    private val summaryTextRepository: RepositoryCacheManager<*, *>,
) : GlobalRepositoryCacheManager {
    override fun startCacheCleaner() {
        weatherDataRepository.startCacheCleaner()
        airQualityRepository.startCacheCleaner()
        rainViewerRepository.startCacheCleaner()
        summaryTextRepository.startCacheCleaner()
    }

    override fun stopCacheCleaner() {
        weatherDataRepository.stopCacheCleaner()
        airQualityRepository.stopCacheCleaner()
        rainViewerRepository.stopCacheCleaner()
        summaryTextRepository.stopCacheCleaner()
    }
}

interface GlobalRepositoryCacheManager {
    fun startCacheCleaner()

    fun stopCacheCleaner()
}
