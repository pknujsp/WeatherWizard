package io.github.pknujsp.weatherwizard.core.data.aqicn

import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity

interface AirQualityRepository {
    suspend fun getAirQuality(latitude: Double, longitude: Double): Result<AirQualityEntity>
    suspend fun getAirQualityByBytes(latitude: Double, longitude: Double): Result<ByteArray>
}