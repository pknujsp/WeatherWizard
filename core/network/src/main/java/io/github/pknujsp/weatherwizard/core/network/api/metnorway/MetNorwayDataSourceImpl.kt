package io.github.pknujsp.weatherwizard.core.network.api.metnorway

import android.util.LruCache
import io.github.pknujsp.weatherwizard.core.model.weather.metnorway.parameter.MetNorwayRequestParameter
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.parser.MetNorwayParser
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayHourlyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class MetNorwayDataSourceImpl @Inject constructor(
    private val metNorwayNetworkApi: MetNorwayNetworkApi
) : MetNorwayDataSource {

    private val forecastRequestStateMap = LruCache<Long, MutableStateFlow<ForecastRequestState>>(6)
    private val mutex = Mutex()

    private suspend fun request(parameter: MetNorwayRequestParameter) {
        mutex.withLock {
            if (forecastRequestStateMap.get(parameter.requestId) != null) {
                forecastRequestStateMap[parameter.requestId]!!.value.registerWaiting()
                return
            }
            forecastRequestStateMap.put(parameter.requestId, MutableStateFlow(ForecastRequestState.Waiting(parameter.requestId)))
        }

        val response = metNorwayNetworkApi.getLocationForecast(parameter.latitude, parameter.longitude).onResult()

        response.onSuccess {
            MetNorwayParser.run {
                val current = it.toCurrentWeather()
                val hourlyForecast = it.toHourlyForecast()
                val dailyForecast = it.toDailyForecast()

                mutex.withLock {
                    forecastRequestStateMap[parameter.requestId]!!.value = ForecastRequestState.Success(
                        Response(
                            currentWeather = current,
                            hourlyForecasts = hourlyForecast,
                            dailyForecasts = dailyForecast,
                        ),
                        parameter.requestId,
                    )
                }
            }
        }.onFailure {
            mutex.withLock {
                forecastRequestStateMap[parameter.requestId]!!.value = ForecastRequestState.Failure(it, parameter.requestId)
            }
        }
    }

    private data class Response(
        val currentWeather: MetNorwayCurrentWeatherResponse,
        val hourlyForecasts: MetNorwayHourlyForecastResponse,
        val dailyForecasts: MetNorwayDailyForecastResponse,
    ) {
        private var consumed: Int = 0
        private val requestCount: Int = 3

        fun consume() {
            consumed++
        }

        fun isAllConsumed(): Boolean = consumed == requestCount
    }

    private sealed class ForecastRequestState {
        var requestCount: Int = 0
        abstract val requestId: Long

        companion object {
            val mutex = Mutex()
        }

        fun registerWaiting() {
            requestCount++
        }

        data class Waiting(override val requestId: Long) : ForecastRequestState()

        data class Success(
            val response: Response,
            override val requestId: Long,
        ) : ForecastRequestState()

        data class Failure(
            val throwable: Throwable,
            override val requestId: Long,
        ) : ForecastRequestState()

    }

    private suspend fun ForecastRequestState.onResponse(): Result<Response> {
        return when (this) {
            is ForecastRequestState.Success -> {
                ForecastRequestState.mutex.withLock {
                    response.consume()
                }
                Result.success(response)
            }

            is ForecastRequestState.Failure -> Result.failure(throwable)
            else -> Result.failure(Throwable("Unknown error"))
        }
    }

    override suspend fun getCurrentWeather(parameter: MetNorwayRequestParameter): Result<MetNorwayCurrentWeatherResponse> {
        request(parameter)
        return mutex.withLock { forecastRequestStateMap[parameter.requestId]!! }.filter { it !is ForecastRequestState.Waiting }.first()
            .onResponse().map { it.currentWeather }
    }

    override suspend fun getHourlyForecast(parameter: MetNorwayRequestParameter): Result<MetNorwayHourlyForecastResponse> {
        request(parameter)
        return mutex.withLock { forecastRequestStateMap[parameter.requestId]!! }.filter { it !is ForecastRequestState.Waiting }.first()
            .onResponse().map { it.hourlyForecasts }
    }

    override suspend fun getDailyForecast(parameter: MetNorwayRequestParameter): Result<MetNorwayDailyForecastResponse> {
        request(parameter)
        return mutex.withLock { forecastRequestStateMap[parameter.requestId]!! }.filter { it !is ForecastRequestState.Waiting }.first()
            .onResponse().map { it.dailyForecasts }
    }


}