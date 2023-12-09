package io.github.pknujsp.weatherwizard.core.data.cache

import android.util.Log
import android.util.LruCache
import androidx.core.util.lruCache
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

/**
 * CacheManager
 *
 * defaultCacheExpiryTime: 기본 캐시 만료 시간
 * cleaningInterval: 캐시 청소 주기
 * cacheMaxSize: 캐시 최대 크기
 * dispatcher: 캐시 청소를 위한 코루틴 디스패처
 */
internal class CacheManagerImpl<T : Any>(
    cacheExpiryTime: Duration = Duration.ofMinutes(5),
    cleaningInterval: Duration = Duration.ofMinutes(5),
    cacheMaxSize: Int = 15,
    dispatcher: CoroutineDispatcher
) : CacheManager<T>(cacheExpiryTime.toMillis(), cleaningInterval.toMillis(), cacheMaxSize),
    CoroutineScope by CoroutineScope(dispatcher) {

    private val cacheActor = cacheManagerActor()
    private var cacheCleanerJob: Job? = null
    private val isCacheCleanerRunning = AtomicBoolean(false)
    private val waitTimeForCacheCleaning = 20L

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
                Log.d("CacheManager", "캐시 정리 시작, ${isCacheCleanerRunning.get()}")
                isCacheCleanerRunning.getAndSet(true)
                cacheActor.send(CacheMessage.Clear(CompletableDeferred<Unit>().apply {
                    await()
                    isCacheCleanerRunning.getAndSet(false)
                }))
                Log.d("CacheManager", "캐시 정리 완료, ${isCacheCleanerRunning.get()}")
            }
        }
    }

    override fun stopCacheCleaner() {
        Log.d("CacheManager", "${this@CacheManagerImpl} - StoppedCacheCleaner")
        CoroutineScope(SupervisorJob()).launch {
            while (isCacheCleanerRunning.get()) {
                delay(waitTimeForCacheCleaning)
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
        val cacheMap = lruCache<String, Cache<T>>(cacheMaxSize)
        for (msg in channel) {
            msg.process(cacheMap)
        }
    }

    private sealed interface CacheMessage<T> {
        fun process(cacheMap: LruCache<String, Cache<T>>)

        data class Put<T>(val key: String, val value: T, val expiryTime: Long) : CacheMessage<T> {
            override fun process(cacheMap: LruCache<String, Cache<T>>) {
                Log.d("CacheManager", "PutCache: $key")
                cacheMap.put(key, Cache(value, expiryTime))
            }
        }

        data class Remove<T>(val key: String) : CacheMessage<T> {
            override fun process(cacheMap: LruCache<String, Cache<T>>) {
                Log.d("CacheManager", "RemoveCache: $key")
                cacheMap.remove(key)
            }
        }

        data class Clear<T>(val response: CompletableDeferred<Unit>) : CacheMessage<T> {
            override fun process(cacheMap: LruCache<String, Cache<T>>) {
                val now = System.currentTimeMillis()
                try {
                    cacheMap.snapshot().forEach { (key, cache) ->
                        if (cache.isExpired(now)) {
                            cacheMap.remove(key)
                        }
                    }
                } catch (e: UnsupportedOperationException) {
                    e.printStackTrace()
                } finally {
                    response.complete(Unit)
                }
            }
        }

        data class Get<T>(
            val key: String, val response: CompletableDeferred<CacheState<T>>
        ) : CacheMessage<T> {
            override fun process(cacheMap: LruCache<String, Cache<T>>) {
                val cache = cacheMap[key]

                response.complete(if (cache?.isExpired() == true) {
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
    fun isExpired(now: Long = System.currentTimeMillis()): Boolean = now - addedTime > cacheExpiryTime
}