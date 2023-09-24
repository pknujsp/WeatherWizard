package io.github.pknujsp.weatherwizard.core.network.api.metnorway

import io.github.pknujsp.weatherwizard.core.network.retrofit.NetworkApiResult
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface MetNorwayNetworkApi {
    @Headers("User-Agent: WeatherWizard")
    @GET("locationforecast/2.0/complete")
    fun getLocationForecast(@Query("lat", encoded = true) latitude: Double, @Query("lon", encoded = true) longitude: Double):
            NetworkApiResult<MetNorwayResponse>
}