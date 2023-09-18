package io.github.pknujsp.weatherwizard.core.data.aqicn

import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity

interface AirQualityRepository {
    suspend fun getAirQuality(latitude: Double, longitude: Double): Result<AirQualityEntity>
}