package io.github.pknujsp.everyweather.core.model.weather.dailyforecast

import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.RainfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedValueType
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class DailyForecastEntity(
    val dayItems: List<DayItem>,
) : WeatherEntityModel() {
    override fun toString(): String {
        return StringBuilder().apply {
            appendLine(
                """
                Daily Forecast
                - 일일 예보입니다. 약 7일간의 날씨 예보입니다.
                
                """.trimIndent(),
            )
            appendLine("Date, Min / Max Temperature, Day Weather, AM / PM Weather")
            for (item in dayItems) {
                append("${LocalDate.parse(item.dateTime.value, DateTimeFormatter.ISO_ZONED_DATE_TIME)}, ")
                appendLine(
                    "${item.minTemperature} / ${item.maxTemperature}, ${
                        if (item.items.size == 1) item.items[0].weatherCondition.value.description else ""
                    }, ${
                        if (item.items.size >= 2) {
                            "${item.items[0].weatherCondition.value.description} / ${
                                item.items[1].weatherCondition.value.description
                            }"
                        } else {
                            ""
                        }
                    }",
                )
            }
        }.toString()
    }

    @Serializable
    data class DayItem(
        val dateTime: DateTimeValueType,
        val minTemperature: TemperatureValueType,
        val maxTemperature: TemperatureValueType,
        val windMinSpeed: WindSpeedValueType = WindSpeedValueType.none,
        val windMaxSpeed: WindSpeedValueType = WindSpeedValueType.none,
        val items: List<Item>,
    ) {
        @Serializable
        data class Item(
            val weatherCondition: WeatherConditionValueType,
            val rainfallVolume: RainfallValueType = RainfallValueType.none,
            val snowfallVolume: SnowfallValueType = SnowfallValueType.none,
            val rainfallProbability: ProbabilityValueType = ProbabilityValueType.none,
            val snowfallProbability: ProbabilityValueType = ProbabilityValueType.none,
            val precipitationVolume: PrecipitationValueType = PrecipitationValueType.none,
            val precipitationProbability: ProbabilityValueType = ProbabilityValueType.none,
        )
    }
}
