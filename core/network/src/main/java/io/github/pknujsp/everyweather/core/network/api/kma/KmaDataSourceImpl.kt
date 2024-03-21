package io.github.pknujsp.everyweather.core.network.api.kma

import io.github.pknujsp.everyweather.core.model.weather.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.everyweather.core.model.weather.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.everyweather.core.model.weather.kma.parameter.KmaHourlyForecastRequestParameter
import io.github.pknujsp.everyweather.core.model.weather.kma.parameter.KmaYesterdayWeatherRequestParameter
import io.github.pknujsp.everyweather.core.network.api.MultipleRequestHelper
import io.github.pknujsp.everyweather.core.network.api.RequestState
import io.github.pknujsp.everyweather.core.network.api.kma.parser.KmaHtmlParser
import io.github.pknujsp.everyweather.core.network.api.onResponse
import io.github.pknujsp.everyweather.core.network.retrofit.onResult
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import org.jsoup.Jsoup
import java.lang.ref.WeakReference
import java.time.ZoneId
import java.time.ZonedDateTime

internal class KmaDataSourceImpl(
    private val kmaNetworkApi: KmaNetworkApi,
    private val kmaHtmlParser: KmaHtmlParser,
) : KmaDataSource {
    private val zoneId = ZoneId.of("Asia/Seoul")
    private val requestHelper = MultipleRequestHelper<Response>()

    override suspend fun getCurrentWeather(parameter: KmaCurrentWeatherRequestParameter): Result<KmaCurrentWeatherResponse> {
        request(parameter.code)
        return requestHelper.get(parameter.code.toLong())?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.currentWeather } ?: Result.failure(Throwable("Unknown error"))
    }

    override suspend fun getHourlyForecast(parameter: KmaHourlyForecastRequestParameter): Result<KmaHourlyForecastResponse> {
        request(parameter.code)
        return requestHelper.get(parameter.code.toLong())?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.hourlyForecasts } ?: Result.failure(Throwable("Unknown error"))
    }

    override suspend fun getDailyForecast(parameter: KmaDailyForecastRequestParameter): Result<KmaDailyForecastResponse> {
        request(parameter.code)
        return requestHelper.get(parameter.code.toLong())?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.dailyForecasts } ?: Result.failure(Throwable("Unknown error"))
    }

    override suspend fun getYesterdayWeather(parameter: KmaYesterdayWeatherRequestParameter): Result<KmaYesterdayWeatherResponse> {
        request(parameter.code)
        return requestHelper.get(parameter.code.toLong())?.filter { it !is RequestState.Waiting }?.first()?.onResponse()
            ?.map { it.yesterdayWeather } ?: Result.failure(Throwable("Unknown error"))
    }

    private suspend fun request(code: String) {
        if (!requestHelper.add(code.toLong())) {
            return
        }
        val currentResponse =
            kmaNetworkApi.getCurrentWeather(code = code).onResult().fold(
                onSuccess = {
                    val parsed =
                        kmaHtmlParser.parseCurrentConditions(
                            document = WeakReference(Jsoup.parse(it)).get()!!,
                            baseDateTime = ZonedDateTime.now(zoneId).toString(),
                        )
                    Result.success(parsed)
                },
                onFailure = { Result.failure(it) },
            )

        val forecastResponse =
            kmaNetworkApi.getHourlyAndDailyForecast(code = code).onResult().fold(
                onSuccess = {
                    val parsedHourlyForecast =
                        kmaHtmlParser.parseHourlyForecasts(
                            document =
                                WeakReference(
                                    Jsoup.parse(it),
                                ).get()!!,
                        )

                    val parsedDailyForecast =
                        kmaHtmlParser.parseDailyForecasts(
                            document =
                                WeakReference(
                                    Jsoup.parse(it),
                                ).get()!!,
                        )
                    Result.success(
                        parsedHourlyForecast to kmaHtmlParser.makeExtendedDailyForecasts(parsedHourlyForecast, parsedDailyForecast),
                    )
                },
                onFailure = {
                    Result.failure(it)
                },
            )

        val result =
            if (currentResponse.isSuccess && forecastResponse.isSuccess) {
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

        requestHelper.update(code.toLong(), result)
    }

    private data class Response(
        val currentWeather: KmaCurrentWeatherResponse,
        val hourlyForecasts: KmaHourlyForecastResponse,
        val dailyForecasts: KmaDailyForecastResponse,
        val yesterdayWeather: KmaYesterdayWeatherResponse,
    )
}
