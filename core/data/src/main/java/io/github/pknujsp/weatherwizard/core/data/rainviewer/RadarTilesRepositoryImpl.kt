package io.github.pknujsp.weatherwizard.core.data.rainviewer

import io.github.pknujsp.weatherwizard.core.data.RepositoryCacheManager
import io.github.pknujsp.weatherwizard.core.data.cache.CacheCleaner
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager
import io.github.pknujsp.weatherwizard.core.model.rainviewer.RadarTiles
import io.github.pknujsp.weatherwizard.core.network.api.rainviewer.RainViewerDataSource
import javax.inject.Inject

class RadarTilesRepositoryImpl @Inject constructor(
    private val rainViewerDataSource: RainViewerDataSource, cacheManager: CacheManager<Long, RadarTiles>, cacheCleaner: CacheCleaner
) : RadarTilesRepository, RepositoryCacheManager<Long, RadarTiles>(cacheCleaner, cacheManager) {

    private var cacheKey = System.currentTimeMillis()

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
                    cacheManager.put(cacheKey, this)
                }
            }
        }
    }

    private suspend fun getCache(
    ): RadarTiles? = when (val cacheState = cacheManager.get(cacheKey)) {
        is CacheManager.CacheState.Hit -> {
            cacheState.value
        }

        else -> {
            cacheKey = System.currentTimeMillis()
            null
        }
    }

}