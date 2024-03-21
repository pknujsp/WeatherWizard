package io.github.pknujsp.everyweather.core.data.rainviewer

import io.github.pknujsp.everyweather.core.model.rainviewer.RadarTiles

interface RadarTilesRepository {
    suspend fun getTiles(): Result<RadarTiles>
}
