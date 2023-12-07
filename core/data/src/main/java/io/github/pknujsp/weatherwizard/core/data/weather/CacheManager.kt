package io.github.pknujsp.weatherwizard.core.data.weather

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.cast
import kotlin.time.toKotlinDuration


class CacheManager<T : Any>(
    private val cacheMaxTime: Duration = Duration.ofMinutes(4),
    private val searchMaxInterval: Duration = Duration.ofSeconds(25),
    cleaningInterval: Duration = Duration.ofMinutes(10)
) {

    private val cacheMap = mutableMapOf<String, Cache<T>>()
    private val lastSearchTimeMap = mutableMapOf<String, LocalDateTime>()
    private val cleaningInterval = cleaningInterval.toKotlinDuration()
    private val mutex = Mutex()

    suspend fun startCacheCleaner() {
        while (true) {
            mutex.withLock {
                if (cacheMap.isNotEmpty()) {
                    val now = LocalDateTime.now()
                    for ((key, value) in cacheMap.entries) {
                        if (value.isExpired(now, cacheMaxTime)) {
                            Log.d("CacheManager cleaner", "Cache deleted: $key")
                            clearExpiredCache(key)
                        }
                    }
                }
            }
            delay(cleaningInterval)
            Log.d("CacheManager cleaner", "$this ${LocalDateTime.now()}")
        }
    }

    suspend fun get(key: String, cls: KClass<T>): CacheState<T> {
        val cache = mutex.withLock { cacheMap[key] } ?: return CacheState.Miss

        val now = LocalDateTime.now()
        val isValidSearch = lastSearchTimeMap[key]?.let { Duration.between(it, now) <= searchMaxInterval } ?: false
        if (!isValidSearch) {
            mutex.withLock {
                lastSearchTimeMap[key] = now
            }
        }

        if (cache.isExpired(now, cacheMaxTime)) {
            clearExpiredCache(key)
            return CacheState.Expired
        }

        return cache.get(cls)?.let { CacheState.Valid(it) } ?: CacheState.Miss
    }

    suspend fun put(key: String, value: T) {
        mutex.withLock {
            cacheMap.getOrPut(key) {
                Cache(LocalDateTime.now())
            }.add(value)
        }
    }

    private suspend fun clearExpiredCache(key: String) {
        mutex.withLock {
            cacheMap[key]?.clear()
            cacheMap.remove(key)
        }
    }

    private data class Cache<T : Any>(
        val added: LocalDateTime
    ) {
        private val mutableMap = mutableMapOf<KClass<out T>, T>()

        fun add(value: T) {
            mutableMap[value::class] = value
        }

        fun get(typeCls: KClass<T>): T? = mutableMap[typeCls]?.run { typeCls.cast(this) }

        fun isExpired(now: LocalDateTime, cacheMaxTime: Duration): Boolean = Duration.between(added, now) > cacheMaxTime

        fun clear() {
            mutableMap.clear()
        }
    }

    sealed interface CacheState<out T> {
        data object Expired : CacheState<Nothing>
        data object Miss : CacheState<Nothing>
        data class Valid<T>(val value: T) : CacheState<T>
    }
}