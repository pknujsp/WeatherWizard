package io.github.pknujsp.weatherwizard.core.network.datasource.kma

import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaNetworkApi
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser.KmaHtmlParser
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser.ParsedKmaDailyForecast
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser.ParsedKmaHourlyForecast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jsoup.Jsoup
import retrofit2.Response
import java.lang.ref.WeakReference
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class KmaDataSourceImpl @Inject constructor(
    private val kmaNetworkApi: KmaNetworkApi,
    private val kmaHtmlParser: KmaHtmlParser,
) : KmaDataSource {

    private val zoneId = ZoneId.of("Asia/Seoul")
    private val forecastRequestStateMap = mutableMapOf<Long, MutableStateFlow<ForecastRequestState>>()
    private val mutex = Mutex()

    override suspend fun getCurrentWeather(code: String, requestId: Long): Result<KmaCurrentWeatherResponse> {
        requestForecast(code, requestId)
        val hourlyForecasts = mutex.withLock { forecastRequestStateMap[requestId]!! }.filter { it !is ForecastRequestState.Waiting }.first()
            .onResponse<KmaHourlyForecastResponse>()

        if (hourlyForecasts.isFailure) return Result.failure(hourlyForecasts.exceptionOrNull()!!)

        return kmaNetworkApi.getCurrentWeather(code = code).onResponse().fold(
            onSuccess = {
                val parsed = kmaHtmlParser.parseCurrentConditions(
                    document = WeakReference(Jsoup.parse(it)).get()!!,
                    baseDateTime = ZonedDateTime.now(zoneId).toString(),
                )

                Result.success(KmaCurrentWeatherResponse(currentWeather = parsed,
                    hourlyForecast = hourlyForecasts.getOrThrow().items.first()))
            },
            onFailure = { Result.failure(it) },
        )
    }

    override suspend fun getHourlyForecast(code: String, requestId: Long): Result<KmaHourlyForecastResponse> {
        requestForecast(code, requestId)
        return mutex.withLock { forecastRequestStateMap[requestId]!! }.filter { it !is ForecastRequestState.Waiting }.first()
            .onResponse()
    }


    override suspend fun getDailyForecast(code: String, requestId: Long): Result<KmaDailyForecastResponse> {
        requestForecast(code, requestId)
        return mutex.withLock { forecastRequestStateMap[requestId]!! }.filter { it !is ForecastRequestState.Waiting }.first()
            .onResponse()
    }


    private suspend fun requestForecast(code: String, requestId: Long) {
        mutex.withLock {
            if (forecastRequestStateMap.containsKey(requestId)) {
                forecastRequestStateMap[requestId]!!.value.wait()
                return
            }
            forecastRequestStateMap[requestId] = MutableStateFlow(ForecastRequestState.Waiting(requestId))
        }

        kmaNetworkApi.getHourlyAndDailyForecast(code = code).onResponse().fold(
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

                mutex.withLock {
                    val waitingCounts = forecastRequestStateMap[requestId]!!.value.requestCount
                    forecastRequestStateMap[requestId]!!.value =
                        ForecastRequestState.Success(ForecastResponse(hourlyForecasts = parsedHourlyForecast,
                            dailyForecasts = parsedDailyForecast,
                            waitingCounts), requestId)
                }
            },
            onFailure = {
                mutex.withLock {
                    forecastRequestStateMap[requestId]!!.value = ForecastRequestState.Failure(it, requestId)
                }
            },
        )
    }

    private fun Response<String>.onResponse(): Result<String> {
        return if (isSuccessful and !body().isNullOrEmpty()) Result.success(body()!!)
        else Result.failure(Throwable(errorBody()?.toString() ?: "Unknown error"))
    }

    private data class ForecastResponse(
        val hourlyForecasts: List<ParsedKmaHourlyForecast>, val dailyForecasts: List<ParsedKmaDailyForecast>, private val requestCount: Int
    ) {
        private var consumed: Int = 0

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

        fun wait() {
            requestCount++
        }

        data class Waiting(override val requestId: Long) : ForecastRequestState()

        data class Success(
            val response: ForecastResponse,
            override val requestId: Long,
        ) : ForecastRequestState()

        data class Failure(
            val throwable: Throwable,
            override val requestId: Long,
        ) : ForecastRequestState()

    }

    private suspend inline fun <reified T : ApiResponseModel> ForecastRequestState.onResponse(): Result<T> {
        return when (this) {
            is ForecastRequestState.Success -> {
                ForecastRequestState.mutex.withLock {
                    response.consume()
                    if (response.isAllConsumed()) {
                        forecastRequestStateMap.remove(requestId)
                    }
                }

                Result.success(response as T)
            }

            is ForecastRequestState.Failure -> Result.failure(throwable)
            else -> Result.failure(Throwable("Unknown error"))
        }
    }
}