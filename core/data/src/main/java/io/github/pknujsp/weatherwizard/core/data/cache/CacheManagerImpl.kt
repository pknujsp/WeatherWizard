package io.github.pknujsp.weatherwizard.core.data.cache

import android.util.LruCache
import androidx.core.util.lruCache
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
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
class CacheManagerImpl<K, V>(
    cacheExpiryTime: Duration = Duration.ofMinutes(5),
    cleaningInterval: Duration = Duration.ofMinutes(5),
    cacheMaxSize: Int = 10,
    dispatcher: CoroutineDispatcher
) : CacheManager<K, V>(cacheExpiryTime.toMillis(), cleaningInterval.toMillis(), cacheMaxSize), CacheCleaner,
    CoroutineScope by CoroutineScope(dispatcher) {

    private val cacheActor = cacheManagerActor()
    private val isCacheCleanerRunning = AtomicBoolean(false)
    private val waitTimeForCacheCleaning = 20L
    private var cacheCleanerJob: Job? = null

    init {
        start()
    }

    override fun start() {
        if (cacheCleanerJob?.isActive == true) {
            return
        }

        cacheCleanerJob = launch(SupervisorJob()) {
            println("CacheManager" + "${this@CacheManagerImpl} - 캐시 클리너 시작")

            while (true) {
                delay(cleaningInterval)

                println("CacheManager" + "캐시 자동 정리 시작")
                isCacheCleanerRunning.getAndSet(true)

                val response = CompletableDeferred<Int>()
                cacheActor.send(CacheMessage.Clear(response))
                response.await()

                isCacheCleanerRunning.getAndSet(false)
                println("CacheManager" + "캐시 자동 정리 완료, ${response.await()}개 삭제됨")
            }
        }
    }

    override fun stop() {
        println("CacheManager" + "${this@CacheManagerImpl} - 캐시 클리너 종료")
        launch {
            while (isCacheCleanerRunning.get()) {
                delay(waitTimeForCacheCleaning)
            }
            cacheCleanerJob?.cancel()
            cacheCleanerJob = null
        }
    }

    override suspend fun get(key: K): CacheState<V> {
        val response = CompletableDeferred<CacheState<V>>()
        cacheActor.send(CacheMessage.Get(key, response))
        return response.await()
    }


    override suspend fun put(key: K, value: V, cacheExpiryTime: Long) {
        cacheActor.send(CacheMessage.Put(key, value, cacheExpiryTime))
    }

    override suspend fun remove(key: K): Boolean {
        val response = CompletableDeferred<Boolean>()
        cacheActor.send(CacheMessage.Remove(key, response))
        return response.await()
    }

    override suspend fun entries(): List<Pair<K, Cache<V>>> {
        val response = CompletableDeferred<List<Pair<K, Cache<V>>>>()
        cacheActor.send(CacheMessage.Entries(response))
        return response.await()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun CoroutineScope.cacheManagerActor(
    ) = actor<CacheMessage<K, V>>(start = CoroutineStart.LAZY) {
        val cacheMap = LruCache<K, Cache<V>>(cacheMaxSize)
        for (msg in channel) {
            msg.process(cacheMap)
        }
    }

    private sealed interface CacheMessage<K, V> {
        fun process(cacheMap: LruCache<K, Cache<V>>)

        data class Put<K, V>(val key: K, val value: V, val expiryTime: Long) : CacheMessage<K, V> {
            override fun process(cacheMap: LruCache<K, Cache<V>>) {
                println("CacheManager" + "캐시 추가: $key")
                cacheMap.put(key, Cache(value, expiryTime))
            }
        }

        data class Remove<K, V>(val key: K, val response: CompletableDeferred<Boolean>) : CacheMessage<K, V> {
            override fun process(cacheMap: LruCache<K, Cache<V>>) {
                println("CacheManager" + "캐시 삭제: $key")
                response.complete(cacheMap.remove(key) != null)
            }
        }

        data class Clear<K, V>(val response: CompletableDeferred<Int>) : CacheMessage<K, V> {
            override fun process(cacheMap: LruCache<K, Cache<V>>) {
                val now = System.currentTimeMillis()
                var removedCount = 0
                cacheMap.snapshot().forEach { (key, cache) ->
                    if (cache.isExpired(now)) {
                        cacheMap.remove(key)
                        removedCount++
                    }
                }
                response.complete(removedCount)
            }
        }

        data class Get<K, V>(
            val key: K, val response: CompletableDeferred<CacheState<V>>
        ) : CacheMessage<K, V> {
            override fun process(cacheMap: LruCache<K, Cache<V>>) {
                val cacheState = cacheMap[key]?.run {
                    if (isExpired()) {
                        CacheState.Miss
                    } else {
                        CacheState.Hit(value)
                    }
                } ?: run {
                    CacheState.Miss
                }
                println("CacheManager" + "캐시 조회: $key, $cacheState")
                response.complete(cacheState)
            }
        }

        data class Entries<K, V>(
            val response: CompletableDeferred<List<Pair<K, Cache<V>>>>
        ) : CacheMessage<K, V> {
            override fun process(cacheMap: LruCache<K, Cache<V>>) {
                response.complete(cacheMap.snapshot().map { it.key to it.value })
            }
        }
    }

}

data class Cache<V>(
    val value: V, val cacheExpiryTime: Long, val addedTime: Long = System.currentTimeMillis()
) {
    fun isExpired(now: Long = System.currentTimeMillis()): Boolean = now - addedTime > cacheExpiryTime
}