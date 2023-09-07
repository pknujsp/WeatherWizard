package io.github.pknujsp.weatherwizard.core.network.datasource.nominatim

import io.github.pknujsp.weatherwizard.core.common.module.AppLocale
import io.github.pknujsp.weatherwizard.core.network.api.nominatim.NominatimNetworkApi
import io.github.pknujsp.weatherwizard.core.network.datasource.nominatim.response.GeoCodeResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.nominatim.response.ReverseGeoCodeResponse
import java.util.Locale
import javax.inject.Inject

class NominatimDataSourceImpl @Inject constructor(
    private val nominatimNetworkApi: NominatimNetworkApi,
    @AppLocale locale: Locale
) : NominatimDataSource {

    private val lang: String = locale.language

    override suspend fun getCode(query: String): Result<GeoCodeResponse> {
        return nominatimNetworkApi.geoCode(query = query, lang = lang).run {
            if (isSuccessful) {
                Result.success(body()!!)
            } else {
                Result.failure(Throwable(errorBody()?.string() ?: "Unknown Error"))
            }
        }
    }

    override suspend fun reverseGeoCode(latitude: Double, longitude: Double): Result<ReverseGeoCodeResponse> {
        return nominatimNetworkApi.reverseGeoCode(latitude = latitude, longitude = longitude, lang = lang).run {
            if (isSuccessful) {
                Result.success(body()!!)
            } else {
                Result.failure(Throwable(errorBody()?.string() ?: "Unknown Error"))
            }
        }
    }

}