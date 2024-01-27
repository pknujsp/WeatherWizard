package io.github.pknujsp.everyweather.core.network.api.kma

import io.github.pknujsp.everyweather.core.model.weather.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.everyweather.core.model.weather.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.everyweather.core.model.weather.kma.parameter.KmaHourlyForecastRequestParameter
import io.github.pknujsp.everyweather.core.model.weather.kma.parameter.KmaYesterdayWeatherRequestParameter

interface KmaDataSource {
    suspend fun getCurrentWeather(parameter: KmaCurrentWeatherRequestParameter): Result<KmaCurrentWeatherResponse>

    suspend fun getHourlyForecast(parameter: KmaHourlyForecastRequestParameter): Result<KmaHourlyForecastResponse>

    suspend fun getDailyForecast(parameter: KmaDailyForecastRequestParameter): Result<KmaDailyForecastResponse>
    suspend fun getYesterdayWeather(parameter: KmaYesterdayWeatherRequestParameter): Result<KmaYesterdayWeatherResponse>
}