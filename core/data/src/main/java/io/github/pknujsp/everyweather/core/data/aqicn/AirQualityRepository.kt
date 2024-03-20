package io.github.pknujsp.everyweather.core.data.aqicn

import io.github.pknujsp.everyweather.core.model.airquality.AirQualityEntity

interface AirQualityRepository {
    suspend fun getAirQuality(
        latitude: Double,
        longitude: Double,
    ): Result<AirQualityEntity>
}
