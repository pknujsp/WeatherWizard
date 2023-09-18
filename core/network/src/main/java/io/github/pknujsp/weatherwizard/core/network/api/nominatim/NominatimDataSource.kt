package io.github.pknujsp.weatherwizard.core.network.api.nominatim

import io.github.pknujsp.weatherwizard.core.network.api.nominatim.response.GeoCodeResponse
import io.github.pknujsp.weatherwizard.core.network.api.nominatim.response.ReverseGeoCodeResponse

interface NominatimDataSource {
    suspend fun getCode(query: String): Result<GeoCodeResponse>

    suspend fun reverseGeoCode(latitude: Double, longitude: Double): Result<ReverseGeoCodeResponse>
}