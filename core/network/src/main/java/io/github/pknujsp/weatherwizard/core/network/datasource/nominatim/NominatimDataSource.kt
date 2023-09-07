package io.github.pknujsp.weatherwizard.core.network.datasource.nominatim

import io.github.pknujsp.weatherwizard.core.network.datasource.nominatim.response.GeoCodeResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.nominatim.response.ReverseGeoCodeResponse

interface NominatimDataSource {
    suspend fun getCode(query: String): Result<GeoCodeResponse>

    suspend fun reverseGeoCode(latitude: Double, longitude: Double): Result<ReverseGeoCodeResponse>
}