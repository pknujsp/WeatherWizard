package io.github.pknujsp.weatherwizard.core.network.api.metnorway

import io.github.pknujsp.weatherwizard.core.network.retrofit.NetworkApiResult
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface MetNorwayNetworkApi {
    @Headers("User-Agent: BestWeatherApp https://github.com/pknujsp")
    @GET("locationforecast/2.0/complete")
    suspend fun getLocationForecast(@Query("lat") latitude: Double, @Query("lon") longitude: Double):
            NetworkApiResult<MetNorwayResponse>
}