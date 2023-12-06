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
    cleaningInterval: Duration = Duration.ofMinutes(5)
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
                            value.clear()
                            cacheMap.remove(key)
                            lastSearchTimeMap.remove(key)
                        }
                    }
                }
            }
            delay(cleaningInterval)
        }
    }

    suspend fun <O : T> get(key: String, cls: KClass<O>): CacheState<out O> {
        val cache = cacheMap[key] ?: return CacheState.Miss

        val now = LocalDateTime.now()
        val isValidSearch = lastSearchTimeMap[key]?.let { Duration.between(it, now) <= searchMaxInterval } ?: false
        if (!isValidSearch) {
            lastSearchTimeMap[key] = now
        }

        if (cache.isExpired(lastSearchTimeMap[key]!!, cacheMaxTime)) {
            cache.clear()
            cacheMap.remove(key)
            return CacheState.Expired
        }

        return cache.cacheList.firstOrNull { cls.isInstance(it) }?.let { CacheState.Valid(cls.cast(it)) } ?: CacheState.Miss
    }

    suspend fun put(key: String, value: T) {
        cacheMap.getOrPut(key) {
            Cache(LocalDateTime.now())
        }.add(value)
    }

    private data class Cache<T>(
        val added: LocalDateTime
    ) {
        private val mutableList: MutableList<T> = mutableListOf()
        val cacheList: List<T>
            get() = mutableList

        fun add(value: T) {
            mutableList.add(value)
        }

        fun isExpired(now: LocalDateTime, cacheMaxTime: Duration): Boolean = Duration.between(added, now) > cacheMaxTime

        fun clear() {
            mutableList.clear()
        }
    }

    sealed interface CacheState<T> {
        data object Expired : CacheState<Nothing>
        data object Miss : CacheState<Nothing>
        data class Valid<T>(val value: T) : CacheState<T>
    }
}