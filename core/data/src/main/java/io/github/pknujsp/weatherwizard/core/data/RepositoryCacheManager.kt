package io.github.pknujsp.weatherwizard.core.data

import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager

abstract class RepositoryCacheManager<T : Any>(protected val cacheManager: CacheManager<T>) {
    fun startCacheCleaner() {
        cacheManager.startCacheCleaner()
    }

    fun stopCacheCleaner() {
        cacheManager.stopCacheCleaner()
    }
}