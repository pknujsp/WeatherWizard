package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManagerImpl
import kotlinx.coroutines.CoroutineDispatcher
import java.time.Duration

object WidgetViewCacheManagerFactory {
    @Volatile private var cacheManager: CacheManager<Int, RemoteViews>? = null

    fun getInstance(dispatcher: CoroutineDispatcher): CacheManager<Int, RemoteViews> {
        return synchronized(this) {
            cacheManager ?: CacheManagerImpl<Int, RemoteViews>(cacheExpiryTime = Duration.ofSeconds(20), dispatcher = dispatcher).also {
                cacheManager = it
            }
        }
    }
}