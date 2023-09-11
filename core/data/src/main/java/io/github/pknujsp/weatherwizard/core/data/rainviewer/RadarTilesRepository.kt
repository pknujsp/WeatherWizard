package io.github.pknujsp.weatherwizard.core.data.rainviewer

import io.github.pknujsp.weatherwizard.core.model.rainviewer.RadarTiles

interface RadarTilesRepository {
    suspend fun getTiles(): Result<RadarTiles>
}