package io.github.pknujsp.weatherwizard.core.network.api.kma


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KmaNetworkApi {
  @GET("current-weather.do")
  suspend fun getCurrentWeather(
    @Query(encoded = true, value = "unit") unit: String = "m%2Fs",
    @Query(encoded = true, value = "aws") aws: String = "N",
    @Query(encoded = true, value = "code") code: String,
  ): Response<String>

  @GET("digital-forecast.do")
  suspend fun getHourlyAndDailyForecast(
    @Query(encoded = true, value = "unit") unit: String = "m%2Fs",
    @Query(encoded = true, value = "hr1") hr1: String = "Y",
    @Query(encoded = true, value = "ext") ext: String = "N",
    @Query(encoded = true, value = "code") code: String,
  ): Response<String>
}