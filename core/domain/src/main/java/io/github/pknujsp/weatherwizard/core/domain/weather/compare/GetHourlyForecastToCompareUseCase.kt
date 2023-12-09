package io.github.pknujsp.weatherwizard.core.domain.weather.compare

import io.github.pknujsp.weatherwizard.core.data.weather.RequestWeatherData
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.ToCompareHourlyForecastEntity
import java.time.ZonedDateTime
import javax.inject.Inject

class GetHourlyForecastToCompareUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetForecastToCompareUseCase<ToCompareHourlyForecastEntity> {
    override suspend fun invoke(
        requests: List<WeatherDataRequest.Request>
    ): Result<ToCompareHourlyForecastEntity> {
        return requests.map { request ->
            request.weatherProvider to weatherDataRepository.getWeatherData(RequestWeatherData(latitude = request.location.latitude,
                longitude = request.location.longitude,
                weatherProvider = request.weatherProvider,
                majorWeatherEntityTypes = request.weatherDataMajorCategories), request.requestId)
        }.let { responses ->
            if (responses.all { it.second.isSuccess }) {
                val entities = responses.map {
                    it.first to (it.second.getOrThrow().list.first() as HourlyForecastEntity).items.mapByWeatherProvider(it.first)
                        .map { item ->
                            ToCompareHourlyForecastEntity.Item(dateTime = item.dateTime,
                                weatherCondition = item.weatherCondition,
                                temperature = item.temperature,
                                feelsLikeTemperature = item.feelsLikeTemperature,
                                humidity = item.humidity,
                                windSpeed = item.windSpeed,
                                windDirection = item.windDirection,
                                rainfallVolume = item.rainfallVolume,
                                snowfallVolume = item.snowfallVolume,
                                rainfallProbability = item.rainfallProbability,
                                snowfallProbability = item.snowfallProbability,
                                precipitationVolume = item.precipitationVolume,
                                precipitationProbability = item.precipitationProbability)
                        }
                }
                Result.success(ToCompareHourlyForecastEntity(entities))
            } else {
                Result.failure(Throwable())
            }
        }
    }

    private fun List<HourlyForecastEntity.Item>.mapByWeatherProvider(weatherProvider: WeatherProvider) =
        if (weatherProvider == WeatherProvider.MetNorway) {
            var lastIdx = 0
            val map = mutableMapOf<String, Long>()

            for (i in (size / 2)..<size) {
                val diff = map.getOrPut(this[i].dateTime.value) {
                    java.time.Duration.ofSeconds(ZonedDateTime.parse(this[i].dateTime.value).toEpochSecond()).toHours()
                } - map.getOrPut(this[i - 1].dateTime.value) {
                    java.time.Duration.ofSeconds(ZonedDateTime.parse(this[i - 1].dateTime.value).toEpochSecond()).toHours()
                }

                if (diff > 1) {
                    lastIdx = i
                    break
                }
            }

            subList(0, lastIdx)
        } else {
            this
        }

}