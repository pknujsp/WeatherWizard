package io.github.pknujsp.everyweather.core.model.weather.hourlyforecast

import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.everyweather.core.model.weather.common.HumidityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.RainfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedValueType
import kotlinx.serialization.Serializable
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class HourlyForecastEntity(
    val items: List<Item>
) : WeatherEntityModel() {

    override fun toString(): String {
        return StringBuilder().apply {
            appendLine("Hourly Forecast")
            appendLine("Time, Weather, Temperature, Precipitation Probability, Precipitation volume, Humidity, Wind Speed")
            for (item in items) {
                append("${LocalDateTime.parse(item.dateTime.value, DateTimeFormatter.ISO_ZONED_DATE_TIME)},")
                append(" ${item.weatherCondition.value.description},")
                append(" ${item.temperature},")
                append(" ${item.precipitationProbability},")
                append(" ${item.precipitationVolume},")
                append(" ${item.humidity},")
                appendLine(" ${item.windSpeed}")
            }
        }.toString()
    }

    @Serializable
    data class Item(
        val dateTime: DateTimeValueType,
        val weatherCondition: WeatherConditionValueType,
        val temperature: TemperatureValueType,
        val feelsLikeTemperature: TemperatureValueType,
        val humidity: HumidityValueType,
        val windSpeed: WindSpeedValueType,
        val windDirection: WindDirectionValueType,
        val rainfallVolume: RainfallValueType,
        val snowfallVolume: SnowfallValueType,
        val rainfallProbability: ProbabilityValueType = ProbabilityValueType.none,
        val snowfallProbability: ProbabilityValueType = ProbabilityValueType.none,
        val precipitationVolume: PrecipitationValueType,
        val precipitationProbability: ProbabilityValueType,
    )
}