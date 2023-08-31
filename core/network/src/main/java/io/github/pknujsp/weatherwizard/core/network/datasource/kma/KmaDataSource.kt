package io.github.pknujsp.weatherwizard.core.network.datasource.kma

interface KmaDataSource {
    suspend fun getCurrentWeather(code: String, requestId: Long): Result<KmaCurrentWeatherResponse>

    suspend fun getHourlyForecast(code: String, requestId: Long): Result<KmaHourlyForecastResponse>

    suspend fun getDailyForecast(code: String, requestId: Long): Result<KmaDailyForecastResponse>
}