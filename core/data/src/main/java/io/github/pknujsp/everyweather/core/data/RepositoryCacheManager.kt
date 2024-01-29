package io.github.pknujsp.everyweather.core.data

import io.github.pknujsp.everyweather.core.data.cache.CacheCleaner
import io.github.pknujsp.everyweather.core.data.cache.CacheManager

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