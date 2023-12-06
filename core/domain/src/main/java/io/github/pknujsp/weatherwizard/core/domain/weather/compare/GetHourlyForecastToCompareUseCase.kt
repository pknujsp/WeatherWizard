package io.github.pknujsp.weatherwizard.core.domain.weather.compare

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.ToCompareHourlyForecastEntity
import java.time.ZonedDateTime
import javax.inject.Inject

class GetHourlyForecastToCompareUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetForecastToCompareUseCase<ToCompareHourlyForecastEntity> {
    override suspend fun invoke(
        latitude: Double, longitude: Double, weatherProviders: List<WeatherProvider>, requestId: Long
    ): Result<ToCompareHourlyForecastEntity> {
        return weatherProviders.mapIndexed { i, provider ->
            weatherDataRepository.getWeatherData(MajorWeatherEntityType.HOURLY_FORECAST, latitude, longitude, provider, requestId + i)
        }.let { responses ->
            if (responses.all { it.isSuccess }) {
                val entities = responses.mapIndexed { i, response ->
                    weatherProviders[i] to (response.getOrThrow() as HourlyForecastEntity).items.subList(weatherProviders[i]).map { item ->
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
                Result.failure(responses.first { it.isFailure }.exceptionOrNull()!!)
            }
        }
    }

    private fun List<HourlyForecastEntity.Item>.subList(weatherProvider: WeatherProvider) =
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