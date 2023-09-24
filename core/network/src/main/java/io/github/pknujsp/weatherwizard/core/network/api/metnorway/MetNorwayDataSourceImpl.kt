package io.github.pknujsp.weatherwizard.core.network.api.metnorway

import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult
import javax.inject.Inject

class MetNorwayDataSourceImpl @Inject constructor(
    private val metNorwayNetworkApi: MetNorwayNetworkApi
) : MetNorwayDataSource {
    override suspend fun getLocationForecast(latitude: Double, longitude: Double): Result<MetNorwayResponse> {
        return metNorwayNetworkApi.getLocationForecast(latitude, longitude).onResult()
    }

}