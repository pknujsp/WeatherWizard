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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * CacheManager
 *
 * defaultCacheExpiryTime: 기본 캐시 만료 시간
 * cleaningInterval: 캐시 청소 주기
 * dispatcher: 캐시 청소를 위한 코루틴 디스패처
 */
internal class CacheManagerImpl<T : Any>(
    defaultCacheExpiryTime: Duration = Duration.ofMinutes(5),
    cleaningInterval: Duration = Duration.ofMinutes(5),
    dispatcher: CoroutineDispatcher
) : CacheManager<T>(defaultCacheExpiryTime.toMillis(), cleaningInterval.toMillis()), CoroutineScope by CoroutineScope(dispatcher) {

    private val cacheActor = cacheManagerActor()
    private var cacheCleanerJob: Job? = null
    private val isCleanerWorking = AtomicBoolean(false)
    private val waitingTimeWhenCleaning = 20L

    init {
        startCacheCleaner()
        cacheActor.invokeOnClose {
            stopCacheCleaner()
        }
    }

    override fun startCacheCleaner() {
        if (cacheCleanerJob?.isActive == true) {
            return
        }

        cacheCleanerJob = CoroutineScope(SupervisorJob()).launch {
            Log.d("CacheManager", "${this@CacheManagerImpl} - StartedCacheCleaner")

            while (true) {
                delay(cleaningInterval)
                Log.d("CacheManager", "캐시 정리 시작, ${isCleanerWorking.get()}")
                isCleanerWorking.getAndSet(true)
                cacheActor.send(CacheMessage.Clear(CompletableDeferred<Unit>().apply {
                    await()
                    isCleanerWorking.getAndSet(false)
                }))
                Log.d("CacheManager", "캐시 정리 완료, ${isCleanerWorking.get()}")
            }
        }
    }

    override fun stopCacheCleaner() {
        Log.d("CacheManager", "${this@CacheManagerImpl} - StoppedCacheCleaner")
        CoroutineScope(SupervisorJob()).launch {
            while (isCleanerWorking.get()) {
                delay(waitingTimeWhenCleaning)
            }
            cacheCleanerJob?.cancel()
        }
    }

    override suspend fun get(key: String): CacheState<T> {
        val response = CompletableDeferred<CacheState<T>>()
        cacheActor.send(CacheMessage.Get(key, response))
        return response.await()
    }


    override suspend fun put(key: String, value: T, cacheExpiryTime: Long) {
        cacheActor.send(CacheMessage.Put(key, value, cacheExpiryTime))
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun CoroutineScope.cacheManagerActor(
    ) = actor<CacheMessage<T>> {
        val cacheMap = mutableMapOf<String, Cache<T>>()
        for (msg in channel) {
            msg.process(cacheMap)
        }
    }

    private sealed interface CacheMessage<T> {
        fun process(cacheMap: MutableMap<String, Cache<T>>)

        data class Put<T>(val key: String, val value: T, val expiryTime: Long) : CacheMessage<T> {
            override fun process(cacheMap: MutableMap<String, Cache<T>>) {
                Log.d("CacheManager", "PutCache: $key")
                cacheMap[key] = Cache(value, expiryTime)
            }
        }

        data class Remove<T>(val key: String) : CacheMessage<T> {
            override fun process(cacheMap: MutableMap<String, Cache<T>>) {
                Log.d("CacheManager", "RemoveCache: $key")
                cacheMap.remove(key)
            }
        }

        data class Clear<T>(val response: CompletableDeferred<Unit>) : CacheMessage<T> {
            override fun process(cacheMap: MutableMap<String, Cache<T>>) {
                val now = System.currentTimeMillis()
                cacheMap.entries.removeIf { (_, cache) -> cache.isExpired(now) }
                response.complete(Unit)
            }
        }

        data class Get<T>(
            val key: String, val response: CompletableDeferred<CacheState<T>>
        ) : CacheMessage<T> {
            override fun process(cacheMap: MutableMap<String, Cache<T>>) {
                val cache = cacheMap[key]

                response.complete(if (cache?.isExpired() == true) {
                    cache.hit()
                    Log.d("CacheManager", "HitCache: $key")
                    CacheState.Hit(cache.value)
                } else {
                    Log.d("CacheManager", "MissCache: $key")
                    cacheMap.remove(key)
                    CacheState.Miss
                })
            }
        }
    }

}

data class Cache<T>(
    val value: T, val cacheExpiryTime: Long, val addedTime: Long = System.currentTimeMillis()
) {
    private val _lastHitTime = AtomicLong(addedTime)
    val lastHitTime: Long
        get() = _lastHitTime.get()

    fun hit(now: Long = System.currentTimeMillis()) {
        _lastHitTime.getAndSet(now)
    }

    fun isExpired(now: Long = System.currentTimeMillis()): Boolean = now - addedTime > cacheExpiryTime

    fun isHitRecently(now: Long = System.currentTimeMillis(), readMaxInterval: Long): Boolean = now - lastHitTime <= readMaxInterval
}