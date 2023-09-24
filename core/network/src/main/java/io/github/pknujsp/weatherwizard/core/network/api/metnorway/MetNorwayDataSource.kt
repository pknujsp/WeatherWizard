package io.github.pknujsp.weatherwizard.core.network.api.metnorway

interface MetNorwayDataSource {

    suspend fun getLocationForecast(latitude: Double, longitude: Double): Result<MetNorwayResponse>

}