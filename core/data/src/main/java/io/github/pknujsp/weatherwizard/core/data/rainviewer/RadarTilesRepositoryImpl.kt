package io.github.pknujsp.weatherwizard.core.data.rainviewer

import android.util.LruCache
import io.github.pknujsp.weatherwizard.core.model.rainviewer.RadarTiles
import io.github.pknujsp.weatherwizard.core.network.api.rainviewer.RainViewerDataSource
import javax.inject.Inject

class RadarTilesRepositoryImpl @Inject constructor(
    private val rainViewerDataSource: RainViewerDataSource
) : RadarTilesRepository {

    private val cache = LruCache<Int, RadarTiles>(1)

    override suspend fun getTiles(): Result<RadarTiles> {
        return rainViewerDataSource.getJson().map {
            if (it.generated in cache.snapshot().keys) {
                return@map cache[it.generated]
            }
            RadarTiles(
                it.generated,
                it.host,
                currentIndex = it.radar.past.size,
                it.run { radar.past + radar.nowcast }.map { data ->
                    RadarTiles.Data(
                        data.path,
                        data.time
                    )
                },
                it.version,
            ).apply {
                cache.put(generated, this)
            }
        }
    }
}