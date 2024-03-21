package io.github.pknujsp.everyweather.core.data.cache

abstract class CacheManager<K, V>(
    protected val defaultCacheExpiryTime: Long,
    protected val cleaningInterval: Long,
    protected val cacheMaxSize: Int,
) {
    abstract suspend fun get(key: K): CacheState<V>

    abstract suspend fun remove(key: K): Boolean

    abstract suspend fun put(
        key: K,
        value: V,
        cacheExpiryTime: Long = defaultCacheExpiryTime,
    )

    abstract suspend fun entries(): List<Pair<K, Cache<V>>>

    sealed interface CacheState<out V> {
        data class Hit<V>(val value: V) : CacheState<V>

        data object Miss : CacheState<Nothing>
    }
}

interface CacheCleaner {
    fun start()

    fun stop()
}
