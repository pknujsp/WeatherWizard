package io.github.pknujsp.weatherwizard.core.network.api.kma


import io.github.pknujsp.weatherwizard.core.network.retrofit.NetworkApiResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KmaNetworkApi {
    @GET("current-weather.do?unit=m%2Fs&aws=N")
    suspend fun getCurrentWeather(
        @Query(encoded = true, value = "code") code: String,
    ): NetworkApiResult<String>

    @GET("digital-forecast.do?unit=m%2Fs&hr1=Y&ext=N")
    suspend fun getHourlyAndDailyForecast(
        @Query(encoded = true, value = "code") code: String,
    ): NetworkApiResult<String>
}