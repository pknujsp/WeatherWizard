package io.github.pknujsp.everyweather.core.data.cache

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CacheManagerImplTest {
    private lateinit var cacheManager: CacheManagerImpl<String, FakeCache>
    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun put_and_get() =
        runTest {
            cacheManager = CacheManagerImpl(dispatcher = testDispatcher)
            val fakeHitCache = FakeCache(1, "test")
            cacheManager.put(fakeHitCache.key, fakeHitCache)

            assert(cacheManager.get(fakeHitCache.key) is CacheManager.CacheState.Hit)
            assert(cacheManager.get("missKey") is CacheManager.CacheState.Miss)
        }

    @Test
    fun test_lru_cache_only() =
        runTest {
            val cacheMaxSize = 3
            cacheManager = CacheManagerImpl(dispatcher = testDispatcher, cacheMaxSize = cacheMaxSize)
            val fakeCaches =
                List(5) {
                    FakeCache(it, "test $it").apply {
                        cacheManager.put(key, this)
                    }
                }

            // 추가한 목록에서 키 0, 1 은 캐시 miss
            // 키 2, 3, 4는 캐시 hit가 되어야 한다
            val missKeys = (0..<(fakeCaches.size - cacheMaxSize)).toList()
            val hitKeys = (fakeCaches.size - cacheMaxSize until fakeCaches.size).toList()

            missKeys.forEach {
                assert(cacheManager.get(fakeCaches[it].key) is CacheManager.CacheState.Miss)
            }

            hitKeys.forEach {
                assert(cacheManager.get(fakeCaches[it].key) is CacheManager.CacheState.Hit)
            }
        }

    @Test
    fun test_time_out_only() =
        runBlocking {
            val cacheExpiryTime = Duration.ofSeconds(1)
            cacheManager = CacheManagerImpl(dispatcher = testDispatcher, cacheExpiryTime = cacheExpiryTime)

            val fakeCaches =
                List(6) {
                    FakeCache(it, "test $it")
                }

            // miss 유도
            val missList = fakeCaches.subList(0, 3)
            // hit 유도
            val hitList = fakeCaches.subList(missList.size, fakeCaches.size)

            missList.forEach {
                cacheManager.put(it.key, it)
            }

            delay(cacheExpiryTime.toMillis())

            hitList.forEach {
                cacheManager.put(it.key, it)
            }

            missList.forEach {
                assert(cacheManager.get(it.key) is CacheManager.CacheState.Miss)
            }
            hitList.forEach {
                assert(cacheManager.get(it.key) is CacheManager.CacheState.Hit)
            }
        }

    @Test
    fun cache_cleaner_removes_expired_items() =
        runBlocking {
            val cacheExpiryTime = Duration.ofMillis(5)
            val cleaningInterval = Duration.ofMillis(10)

            val fakeCaches =
                List(100) {
                    FakeCache(it, "test $it")
                }

            cacheManager =
                CacheManagerImpl(
                    dispatcher = testDispatcher,
                    cacheMaxSize = 100,
                    cacheExpiryTime = cacheExpiryTime,
                    cleaningInterval = cleaningInterval,
                )

            fakeCaches.forEach {
                cacheManager.put(it.key, it)
            }
            // 자동으로 캐시가 정리되어 캐시맵은 비어있어야 한다.
            delay(cleaningInterval.toMillis() + 10)
            assertTrue(cacheManager.entries().isEmpty())
        }

    @Test
    fun items_should_not_be_deleted_if_cache_cleaner_didnt_work() =
        runBlocking {
            val cacheExpiryTime = Duration.ofMillis(5)
            val cleaningInterval = Duration.ofSeconds(10)

            val fakeCaches =
                List(100) {
                    FakeCache(it, "test $it")
                }

            cacheManager =
                CacheManagerImpl(
                    dispatcher = testDispatcher,
                    cacheMaxSize = 100,
                    cacheExpiryTime = cacheExpiryTime,
                    cleaningInterval = cleaningInterval,
                )

            fakeCaches.forEach {
                cacheManager.put(it.key, it)
            }
            // 캐시 클리너가 동작하지 않았으므로 아이템의 개수는 유지되어야 한다
            assertTrue(cacheManager.entries().size == fakeCaches.size)
        }
}

data class FakeCache(val id: Int, val value: String) {
    val key: String = id.toString()
}
