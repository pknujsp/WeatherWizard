package io.github.pknujsp.weatherwizard.core.data.cache

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong


class CacheManager<T : Any>(
    defaultCacheExpiryTime: Duration = Duration.ofMinutes(5),
    readMaxInterval: Duration = Duration.ofSeconds(5),
    cleaningInterval: Duration = Duration.ofMinutes(10),
    dispatcher: CoroutineDispatcher
) : CoroutineScope by CoroutineScope(dispatcher) {

    private val defaultCacheExpiryTime = defaultCacheExpiryTime.toMillis()
    private val readMaxInterval = readMaxInterval.toMillis()
    private val cleaningInterval = cleaningInterval.toMillis()

    private val cacheActor = cacheManagerActor()
    private var cacheCleanerJob: Job? = null

    init {
        startCacheCleaner()
    }

    fun startCacheCleaner() {
        if (cacheCleanerJob?.isActive == true) {
            return
        }
        cacheCleanerJob = CoroutineScope(SupervisorJob()).launch {
            Log.d("CacheManager", "${this@CacheManager} - StartedCacheCleaner")
            while (true) {
                delay(cleaningInterval)
                cacheActor.send(CacheMessage.Clear)
            }
        }
    }

    fun stopCacheCleaner() {
        Log.d("CacheManager", "${this@CacheManager} - StoppedCacheCleaner")
        cacheCleanerJob?.cancel()
    }

    suspend fun <E : T> get(key: String): CacheState<E> {
        val response = CompletableDeferred<CacheState<E>>()
        cacheActor.send(CacheMessage.Get(key, response))
        return response.await()
    }


    suspend fun put(key: String, value: T, cacheExpiryTime: Long = defaultCacheExpiryTime) {
        cacheActor.send(CacheMessage.Put(key, value, cacheExpiryTime))
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun CoroutineScope.cacheManagerActor(
    ) = actor<CacheMessage<T>> {
        val cacheMap = mutableMapOf<String, Cache<T>>()

        for (msg in channel) {
            when (msg) {
                is CacheMessage.Put -> {
                    Log.d("CacheManager", "PutCache: ${msg.key}")
                    cacheMap[msg.key] = Cache(msg.value, msg.expiryTime)
                }

                is CacheMessage.Get -> {
                    val cache = cacheMap[msg.key]

                    msg.response.complete(if (cache?.isExpired() == true) {
                        cache.hit()
                        Log.d("CacheManager", "HitCache: ${msg.key}")
                        CacheState.Hit(cache.value)
                    } else {
                        Log.d("CacheManager", "MissCache: ${msg.key}")
                        cacheMap.remove(msg.key)
                        CacheState.Miss
                    })

                }

                is CacheMessage.Remove -> {
                    Log.d("CacheManager", "RemoveCache: ${msg.key}")
                    cacheMap.remove(msg.key)
                }

                is CacheMessage.Clear -> {
                    val now = System.currentTimeMillis()
                    cacheMap.entries.removeIf { (_, cache) -> cache.isExpired(now) }
                }
            }
        }
    }

    private sealed interface CacheMessage<out T> {
        data class Put<T>(val key: String, val value: T, val expiryTime: Long) : CacheMessage<T>
        data class Remove(val key: String) : CacheMessage<Nothing>
        data object Clear : CacheMessage<Nothing>
        data class Get<T>(
            val key: String, val response: CompletableDeferred<CacheState<T>>
        ) : CacheMessage<T>
    }

    sealed interface CacheState<out T> {
        data class Hit<T>(val value: T) : CacheState<T>
        data object Miss : CacheState<Nothing>
    }
}

data class Cache<T>(
    val value: T, val cacheExpiryTime: Long, val addedTime: Long = System.currentTimeMillis()
) {
    private val _lastHitTime = AtomicLong(addedTime)
    val lastHitTime: Long
        get() = _lastHitTime.get()

    fun hit(now: Long = System.currentTimeMillis()) {
        _lastHitTime.set(now)
    }

    fun isExpired(now: Long = System.currentTimeMillis()): Boolean = now - addedTime > cacheExpiryTime

    fun isHitRecently(now: Long = System.currentTimeMillis(), readMaxInterval: Long): Boolean = now - lastHitTime <= readMaxInterval
}