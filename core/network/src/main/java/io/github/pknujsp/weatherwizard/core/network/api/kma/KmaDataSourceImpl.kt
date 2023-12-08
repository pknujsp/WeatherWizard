package io.github.pknujsp.weatherwizard.core.network.api.kma

import android.util.Log
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaHourlyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaYesterdayWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.network.api.MultipleRequestHelper
import io.github.pknujsp.weatherwizard.core.network.api.RequestState
import io.github.pknujsp.weatherwizard.core.network.api.kma.parser.KmaHtmlParser
import io.github.pknujsp.weatherwizard.core.network.api.onResponse
import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
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
    private val requestHelper = MultipleRequestHelper<Response>()

    override suspend fun getCurrentWeather(parameter: KmaCurrentWeatherRequestParameter): Result<KmaCurrentWeatherResponse> {
        request(parameter.code, parameter.requestId)
        return requestHelper.get(parameter.requestId)?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.currentWeather } ?: Result.failure(Throwable("Unknown error"))
    }

    override suspend fun getHourlyForecast(parameter: KmaHourlyForecastRequestParameter): Result<KmaHourlyForecastResponse> {
        request(parameter.code, parameter.requestId)
        return requestHelper.get(parameter.requestId)?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.hourlyForecasts } ?: Result.failure(Throwable("Unknown error"))
    }

    override suspend fun getDailyForecast(parameter: KmaDailyForecastRequestParameter): Result<KmaDailyForecastResponse> {
        request(parameter.code, parameter.requestId)
        return requestHelper.get(parameter.requestId)?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.dailyForecasts } ?: Result.failure(Throwable("Unknown error"))
    }

    override suspend fun getYesterdayWeather(parameter: KmaYesterdayWeatherRequestParameter): Result<KmaYesterdayWeatherResponse> {
        request(parameter.code, parameter.requestId)
        return requestHelper.get(parameter.requestId)?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.yesterdayWeather } ?: Result.failure(Throwable("Unknown error"))
    }


    private suspend fun request(code: String, requestId: Long) {
        if (!requestHelper.add(requestId)) {
            return
        }
        Log.d("KmaDataSourceImpl", "request $requestId, code $code")

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

        val result = if (currentResponse.isSuccess && forecastResponse.isSuccess) {
            val current = currentResponse.getOrThrow()
            val hourly = forecastResponse.getOrThrow().first
            val daily = forecastResponse.getOrThrow().second

            RequestState.Responsed(
                Response(
                    currentWeather = KmaCurrentWeatherResponse(currentWeather = current, hourlyForecast = hourly.first()),
                    hourlyForecasts = KmaHourlyForecastResponse(hourly),
                    dailyForecasts = KmaDailyForecastResponse(daily),
                    yesterdayWeather = KmaYesterdayWeatherResponse(current),
                ),
            )
        } else {
            val cause = "${currentResponse.exceptionOrNull()?.message} ${forecastResponse.exceptionOrNull()?.message}"
            RequestState.Failure(
                throwable = Throwable(cause),
            )
        }

        requestHelper.update(requestId, result)
    }

    private data class Response(
        val currentWeather: KmaCurrentWeatherResponse,
        val hourlyForecasts: KmaHourlyForecastResponse,
        val dailyForecasts: KmaDailyForecastResponse,
        val yesterdayWeather: KmaYesterdayWeatherResponse
    )
}