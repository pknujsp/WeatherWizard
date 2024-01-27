package io.github.pknujsp.everyweather.core.network.api.metnorway

import io.github.pknujsp.everyweather.core.model.weather.metnorway.parameter.MetNorwayRequestParameter
import io.github.pknujsp.everyweather.core.network.api.metnorway.response.MetNorwayCurrentWeatherResponse
import io.github.pknujsp.everyweather.core.network.api.metnorway.response.MetNorwayDailyForecastResponse
import io.github.pknujsp.everyweather.core.network.api.metnorway.response.MetNorwayHourlyForecastResponse

interface MetNorwayDataSource {

    suspend fun getCurrentWeather(parameter: MetNorwayRequestParameter): Result<MetNorwayCurrentWeatherResponse>

    suspend fun getHourlyForecast(parameter: MetNorwayRequestParameter): Result<MetNorwayHourlyForecastResponse>

    suspend fun getDailyForecast(parameter: MetNorwayRequestParameter): Result<MetNorwayDailyForecastResponse>

}