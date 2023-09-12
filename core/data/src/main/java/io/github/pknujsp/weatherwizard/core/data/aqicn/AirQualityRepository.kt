package io.github.pknujsp.weatherwizard.core.data.aqicn

interface AirQualityRepository {
    suspend fun getAirQuality(latitude: Double, longitude: Double): Result<AirQualityResponse>
}