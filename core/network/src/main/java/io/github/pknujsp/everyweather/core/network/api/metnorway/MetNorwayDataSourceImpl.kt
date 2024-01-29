package io.github.pknujsp.everyweather.core.network.api.metnorway

import io.github.pknujsp.everyweather.core.model.weather.metnorway.parameter.MetNorwayRequestParameter
import io.github.pknujsp.everyweather.core.network.api.MultipleRequestHelper
import io.github.pknujsp.everyweather.core.network.api.RequestState
import io.github.pknujsp.everyweather.core.network.api.metnorway.parser.MetNorwayParser
import io.github.pknujsp.everyweather.core.network.api.metnorway.response.MetNorwayCurrentWeatherResponse
import io.github.pknujsp.everyweather.core.network.api.metnorway.response.MetNorwayDailyForecastResponse
import io.github.pknujsp.everyweather.core.network.api.metnorway.response.MetNorwayHourlyForecastResponse
import io.github.pknujsp.everyweather.core.network.api.onResponse
import io.github.pknujsp.everyweather.core.network.retrofit.onResult
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

internal class MetNorwayDataSourceImpl(
    private val metNorwayNetworkApi: MetNorwayNetworkApi
) : MetNorwayDataSource {

    private val requestHelper = MultipleRequestHelper<Response>()

    private suspend fun request(parameter: MetNorwayRequestParameter) {
        if (!requestHelper.add(parameter.requestId)) {
            return
        }

        val response = metNorwayNetworkApi.getLocationForecast(parameter.latitude, parameter.longitude).onResult()
        val result = response.fold(onSuccess = {
            MetNorwayParser.run {
                val current = it.toCurrentWeather()
                val hourlyForecast = it.toHourlyForecast()
                val dailyForecast = it.toDailyForecast()
                RequestState.Responsed(
                    Response(
                        currentWeather = current,
                        hourlyForecasts = hourlyForecast,
                        dailyForecasts = dailyForecast,
                    ),
                )
            }
        }, onFailure = { RequestState.Failure(it) })

        requestHelper.update(parameter.requestId, result)
    }

    private data class Response(
        val currentWeather: MetNorwayCurrentWeatherResponse,
        val hourlyForecasts: MetNorwayHourlyForecastResponse,
        val dailyForecasts: MetNorwayDailyForecastResponse,
    )

    override suspend fun getCurrentWeather(parameter: MetNorwayRequestParameter): Result<MetNorwayCurrentWeatherResponse> {
        request(parameter)
        return requestHelper.get(parameter.requestId)?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.currentWeather } ?: Result.failure(Throwable("Unknown error"))
    }

    override suspend fun getHourlyForecast(parameter: MetNorwayRequestParameter): Result<MetNorwayHourlyForecastResponse> {
        request(parameter)
        return requestHelper.get(parameter.requestId)?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.hourlyForecasts } ?: Result.failure(Throwable("Unknown error"))
    }

    override suspend fun getDailyForecast(parameter: MetNorwayRequestParameter): Result<MetNorwayDailyForecastResponse> {
        request(parameter)
        return requestHelper.get(parameter.requestId)?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.dailyForecasts } ?: Result.failure(Throwable("Unknown error"))
    }

}