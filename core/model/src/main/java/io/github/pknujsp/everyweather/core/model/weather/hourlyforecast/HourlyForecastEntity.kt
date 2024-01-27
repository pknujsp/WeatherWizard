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

@Serializable
data class HourlyForecastEntity(
    val items: List<Item>
) : WeatherEntityModel() {

    override fun toString(): String {
        return StringBuilder().apply {
            append("## 시간별 예보")
            appendLine()
            append("| 시간 | 날씨 | 기온 | 강수확률 | 강수량 | 습도 | 풍속 |")
            appendLine()
            append("| --- | --- | --- | --- | --- | --- | --- |")
            appendLine()
            for (item in items) {
                append("${item.dateTime} | ${item.weatherCondition.value.description} | ${item.temperature} | ${item.precipitationProbability} | ${item.precipitationVolume} | ${item.humidity} | ${item.windSpeed}\n")
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