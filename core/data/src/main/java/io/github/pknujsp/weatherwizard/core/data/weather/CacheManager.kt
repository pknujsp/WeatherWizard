package io.github.pknujsp.weatherwizard.core.data.weather

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong


class CacheManager<T : Any>(
    defaultCacheMaxTime: Duration = Duration.ofMinutes(5),
    searchMaxInterval: Duration = Duration.ofSeconds(25),
    cleaningInterval: Duration = Duration.ofMinutes(10)
) {

    private val defaultCacheMaxTime = defaultCacheMaxTime.toMillis()
    private val searchMaxInterval = searchMaxInterval.toMillis()
    private val cleaningInterval = cleaningInterval.toMillis()

    private val cacheMap = mutableMapOf<String, Cache<T>>()
    private val mutex = Mutex()

    suspend fun startCacheCleaner() {
        while (true) {
            mutex.withLock {
                if (cacheMap.isNotEmpty()) {
                    val now = System.currentTimeMillis()
                    cacheMap.entries.removeIf { (_, cache) ->
                        !cache.isRecentlyHitted(now, searchMaxInterval) and cache.isExpired(now)
                    }
                }
            }
            delay(cleaningInterval)
            Log.d("CacheManager cleaner", "$this ${LocalDateTime.now()}")
        }
    }

    suspend fun <E : T> get(key: String): CacheState<E> {
        val cache = mutex.withLock { cacheMap[key] } ?: return CacheState.Miss

        val now = System.currentTimeMillis()
        return if (cache.isRecentlyHitted(now, searchMaxInterval) or !cache.isExpired(now)) {
            (cache.value as? E)?.let {
                cache.lastHitTime.set(now)
                Log.d("CacheManager", "hit $key")
                CacheState.Hit(it)
            } ?: CacheState.Miss
        } else {
            clearCache(key)
            Log.d("CacheManager", "miss $key")
            CacheState.Miss
        }
    }

    suspend fun put(key: String, value: T, cacheMaxTimeInMinutes: Long = defaultCacheMaxTime) {
        mutex.withLock {
            Log.d("CacheManager", "put $key")
            cacheMap[key] = Cache(value, cacheMaxTimeInMinutes)
        }
    }

    private suspend fun clearCache(key: String) {
        mutex.withLock {
            cacheMap.remove(key)
        }
    }

    private data class Cache<T : Any>(
        val value: T, val cacheMaxTime: Long, val addedTime: Long = System.currentTimeMillis()
    ) {
        val lastHitTime = AtomicLong(addedTime)

        fun isExpired(now: Long): Boolean = now - addedTime > cacheMaxTime

        fun isRecentlyHitted(now: Long, searchMaxInterval: Long): Boolean = now - lastHitTime.get() <= searchMaxInterval
    }

    sealed interface CacheState<out T> {
        data object Miss : CacheState<Nothing>
        data class Hit<T>(val value: T) : CacheState<T>
    }
}