package io.github.pknujsp.weatherwizard.core.network.api.kma

import android.util.LruCache
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaHourlyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaYesterdayWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.network.api.kma.parser.KmaHtmlParser
import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jsoup.Jsoup
import java.lang.ref.WeakReference
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class KmaDataSourceImpl @Inject constructor(
    private val kmaNetworkApi: KmaNetworkApi,
    private val kmaHtmlParser: KmaHtmlParser,
) : KmaDataSource {

    private val zoneId = ZoneId.of("Asia/Seoul")
    private val forecastRequestStateMap = LruCache<Long, MutableStateFlow<ForecastRequestState>>(3)
    private val mutex = Mutex()

    override suspend fun getCurrentWeather(parameter: KmaCurrentWeatherRequestParameter): Result<KmaCurrentWeatherResponse> {
        request(parameter.code, parameter.requestId)
        return mutex.withLock { forecastRequestStateMap[parameter.requestId]!! }.filter { it !is ForecastRequestState.Waiting }.first()
            .onResponse().map { it.currentWeather }
    }

    override suspend fun getHourlyForecast(parameter: KmaHourlyForecastRequestParameter): Result<KmaHourlyForecastResponse> {
        request(parameter.code, parameter.requestId)
        return mutex.withLock { forecastRequestStateMap[parameter.requestId]!! }.filter { it !is ForecastRequestState.Waiting }.first()
            .onResponse().map { it.hourlyForecasts }
    }

    override suspend fun getDailyForecast(parameter: KmaDailyForecastRequestParameter): Result<KmaDailyForecastResponse> {
        request(parameter.code, parameter.requestId)
        return mutex.withLock { forecastRequestStateMap[parameter.requestId]!! }.filter { it !is ForecastRequestState.Waiting }.first()
            .onResponse().map { it.dailyForecasts }
    }

    override suspend fun getYesterdayWeather(parameter: KmaYesterdayWeatherRequestParameter): Result<KmaYesterdayWeatherResponse> {
        request(parameter.code, parameter.requestId)
        return mutex.withLock { forecastRequestStateMap[parameter.requestId]!! }.filter { it !is ForecastRequestState.Waiting }.first()
            .onResponse().map { it.yesterdayWeather }
    }


    private suspend fun request(code: String, requestId: Long) {
        mutex.withLock {
            if (forecastRequestStateMap.get(requestId) != null) {
                forecastRequestStateMap[requestId]!!.value.registerWaiting()
                return
            }
            forecastRequestStateMap.put(requestId, MutableStateFlow(ForecastRequestState.Waiting(requestId)))
        }

        // 현재날씨
        val currentResponse = kmaNetworkApi.getCurrentWeather(code = code).onResult().fold(
            onSuccess = {
                val parsed = kmaHtmlParser.parseCurrentConditions(
                    document = WeakReference(Jsoup.parse(it)).get()!!,
                    baseDateTime = ZonedDateTime.now(zoneId).toString(),
                )
                Result.success(parsed)
            },
            onFailure = { Result.failure(it) },
        )

        val forecastResponse = kmaNetworkApi.getHourlyAndDailyForecast(code = code).onResult().fold(
            onSuccess = {
                val parsedHourlyForecast = kmaHtmlParser.parseHourlyForecasts(
                    document = WeakReference(
                        Jsoup.parse(it),
                    ).get()!!,
                )

                var parsedDailyForecast = kmaHtmlParser.parseDailyForecasts(
                    document = WeakReference(
                        Jsoup.parse(it),
                    ).get()!!,
                )
                parsedDailyForecast = kmaHtmlParser.makeExtendedDailyForecasts(parsedHourlyForecast, parsedDailyForecast.toMutableList())
                Result.success(parsedHourlyForecast to parsedDailyForecast)
            },
            onFailure = {
                Result.failure(it)
            },
        )

        mutex.withLock {
            forecastRequestStateMap[requestId]!!.value = if (currentResponse.isSuccess && forecastResponse.isSuccess) {
                val current = currentResponse.getOrThrow()
                val hourly = forecastResponse.getOrThrow().first
                val daily = forecastResponse.getOrThrow().second

                ForecastRequestState.Success(
                    Response(
                        currentWeather = KmaCurrentWeatherResponse(currentWeather = current,
                            hourlyForecast = hourly.first()),
                        hourlyForecasts = KmaHourlyForecastResponse(hourly),
                        dailyForecasts = KmaDailyForecastResponse(daily),
                        yesterdayWeather = KmaYesterdayWeatherResponse(current),
                    ),
                    requestId,
                )
            } else {
                val cause = "${currentResponse.exceptionOrNull()?.message} ${forecastResponse.exceptionOrNull()?.message}"
                ForecastRequestState.Failure(
                    throwable = Throwable(cause),
                    requestId = requestId,
                )
            }

        }

    }

    private data class Response(
        val currentWeather: KmaCurrentWeatherResponse,
        val hourlyForecasts: KmaHourlyForecastResponse,
        val dailyForecasts: KmaDailyForecastResponse,
        val yesterdayWeather: KmaYesterdayWeatherResponse
    ) {
        private var consumed: Int = 0
        private val requestCount: Int = 4

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
}