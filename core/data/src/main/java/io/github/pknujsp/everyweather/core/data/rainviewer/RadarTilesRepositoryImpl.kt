package io.github.pknujsp.everyweather.core.data.rainviewer

import io.github.pknujsp.everyweather.core.data.RepositoryCacheManager
import io.github.pknujsp.everyweather.core.data.cache.CacheCleaner
import io.github.pknujsp.everyweather.core.data.cache.CacheManager
import io.github.pknujsp.everyweather.core.model.rainviewer.RadarTiles
import io.github.pknujsp.everyweather.core.network.api.rainviewer.RainViewerDataSource

internal class RadarTilesRepositoryImpl(
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