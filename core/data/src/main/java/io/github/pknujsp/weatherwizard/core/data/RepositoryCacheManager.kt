package io.github.pknujsp.weatherwizard.core.data

import io.github.pknujsp.weatherwizard.core.data.cache.CacheCleaner
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity

abstract class RepositoryCacheManager<K, V>(
    protected val cacheCleaner: CacheCleaner,
    protected val cacheManager: CacheManager<K, V>
) {
    fun startCacheCleaner() {
        cacheCleaner.start()
    }

    fun stopCacheCleaner() {
        cacheCleaner.stop()
    }
}