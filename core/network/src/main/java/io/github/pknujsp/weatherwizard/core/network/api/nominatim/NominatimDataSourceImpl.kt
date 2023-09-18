package io.github.pknujsp.weatherwizard.core.network.api.nominatim

import io.github.pknujsp.weatherwizard.core.common.module.AppLocale
import io.github.pknujsp.weatherwizard.core.network.api.nominatim.NominatimNetworkApi
import io.github.pknujsp.weatherwizard.core.network.api.nominatim.response.GeoCodeResponse
import io.github.pknujsp.weatherwizard.core.network.api.nominatim.response.ReverseGeoCodeResponse
import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult
import java.util.Locale
import javax.inject.Inject

class NominatimDataSourceImpl @Inject constructor(
    private val nominatimNetworkApi: NominatimNetworkApi,
    @AppLocale locale: Locale
) : NominatimDataSource {

    private val lang: String = locale.language

    override suspend fun getCode(query: String): Result<GeoCodeResponse> =
        nominatimNetworkApi.geoCode(query = query, lang = lang).onResult()


    override suspend fun reverseGeoCode(latitude: Double, longitude: Double): Result<ReverseGeoCodeResponse> =
        nominatimNetworkApi.reverseGeoCode(latitude = latitude, longitude = longitude, lang = lang).onResult()

}