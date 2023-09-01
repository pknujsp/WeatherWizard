package io.github.pknujsp.weatherwizard.core.network.datasource.kma

import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parameter.KmaHourlyForecastRequestParameter

interface KmaDataSource {
    suspend fun getCurrentWeather(parameter: KmaCurrentWeatherRequestParameter): Result<KmaCurrentWeatherResponse>

    suspend fun getHourlyForecast(parameter: KmaHourlyForecastRequestParameter): Result<KmaHourlyForecastResponse>

    suspend fun getDailyForecast(parameter: KmaDailyForecastRequestParameter): Result<KmaDailyForecastResponse>
}