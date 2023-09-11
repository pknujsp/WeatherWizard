package io.github.pknujsp.weatherwizard.core.network.api.rainviewer

import io.github.pknujsp.weatherwizard.core.network.retrofit.NetworkApiResult
import retrofit2.http.GET

interface RainViewerNetworkApi {

    @GET("weather-maps.json")
    suspend fun getJson(): NetworkApiResult<RainViewerResponse>
}