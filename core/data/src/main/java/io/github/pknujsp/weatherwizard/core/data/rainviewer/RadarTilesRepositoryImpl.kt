package io.github.pknujsp.weatherwizard.core.data.rainviewer

import android.util.LruCache
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializer
import io.github.pknujsp.weatherwizard.core.data.weather.CacheManager
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity
import io.github.pknujsp.weatherwizard.core.model.rainviewer.RadarTiles
import io.github.pknujsp.weatherwizard.core.network.api.rainviewer.RainViewerDataSource
import javax.inject.Inject

class RadarTilesRepositoryImpl @Inject constructor(
    private val rainViewerDataSource: RainViewerDataSource, private val cacheManager: CacheManager<RadarTiles>
) : RadarTilesRepository, RepositoryInitializer {

    private val cache = LruCache<Int, RadarTiles>(1)
    private var cacheKeyString = System.currentTimeMillis().toString()

    override suspend fun getTiles(): Result<RadarTiles> {
        return getCache()?.run {
            Result.success(this)
        } ?: run {
            rainViewerDataSource.getJson().map {
                RadarTiles(
                    it.generated,
                    it.host,
                    currentIndex = it.radar.past.size,
                    it.run { radar.past + radar.nowcast }.map { data ->
                        RadarTiles.Data(data.path, data.time)
                    },
                    it.version,
                ).apply {
                    cacheManager.put(cacheKeyString, this)
                }
            }
        }
    }

    private suspend fun getCache(
    ): RadarTiles? = when (val cacheState = cacheManager.get<RadarTiles>(cacheKeyString)) {
        is CacheManager.CacheState.Hit -> {
            cacheState.value
        }

        else -> {
            cacheKeyString = System.currentTimeMillis().toString()
            null
        }
    }

    override suspend fun initialize() {
        cacheManager.startCacheCleaner()
    }
}