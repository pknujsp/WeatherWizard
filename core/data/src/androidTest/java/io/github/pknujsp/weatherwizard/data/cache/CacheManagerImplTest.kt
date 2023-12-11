package io.github.pknujsp.weatherwizard.data.cache

import io.github.pknujsp.weatherwizard.core.data.cache.CacheManagerImpl
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CacheManagerImplTest {
    val dispatcher = UnconfinedTestDispatcher()
    val cacheManagerImpl: CacheManagerImpl<FakeCacheData> = CacheManagerImpl(dispatcher = dispatcher)

    @Before
    fun setUp() {
        // test coroutine disptacher
        cacheManagerImpl
    }

    @Test
    fun test_thread_blocking() = runTest(dispatcher) {
        val cacheData = FakeCacheData(1)
        cacheManagerImpl.put("1", cacheData)
        val result = cacheManagerImpl.get("1")
        assertEquals(cacheData, result)
    }
}

data class FakeCacheData(val id: Int)