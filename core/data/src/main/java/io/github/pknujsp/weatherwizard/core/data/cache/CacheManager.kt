package io.github.pknujsp.weatherwizard.core.data.cache

abstract class CacheManager<T : Any>(
    protected val defaultCacheExpiryTime: Long,
    protected val cleaningInterval: Long,
) {
    abstract suspend fun <E : T> get(key: String): CacheState<E>

    abstract suspend fun put(key: String, value: T, cacheExpiryTime: Long = defaultCacheExpiryTime)

    abstract fun startCacheCleaner()

    abstract fun stopCacheCleaner()

    sealed interface CacheState<out T> {
        data class Hit<T>(val value: T) : CacheState<T>
        data object Miss : CacheState<Nothing>
    }
}