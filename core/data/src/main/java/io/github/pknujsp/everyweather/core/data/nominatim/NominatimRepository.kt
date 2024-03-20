package io.github.pknujsp.everyweather.core.data.nominatim

import io.github.pknujsp.everyweather.core.model.nominatim.GeoCodeEntity
import io.github.pknujsp.everyweather.core.model.nominatim.ReverseGeoCodeEntity

interface NominatimRepository {
    suspend fun geoCode(query: String): Result<List<GeoCodeEntity>>

    suspend fun reverseGeoCode(
        latitude: Double,
        longitude: Double,
    ): Result<ReverseGeoCodeEntity>
}
