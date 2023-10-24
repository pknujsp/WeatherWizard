package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DetailDailyForecast(
    dailyForecastEntity: DailyForecastEntity,
    units: CurrentUnits
) : UiModel {
    val items: List<Item>
    val displayPrecipitationProbability: Boolean

    init {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")
        val dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE")
        var zonedDateTime:ZonedDateTime

        items = dailyForecastEntity.dayItems.mapIndexed { id, dayItem ->
            zonedDateTime = ZonedDateTime.parse(dayItem.dateTime.value)
            Item(
                id = id,
                date = dateFormatter.format(zonedDateTime),
                dayOfWeek = dayFormatter.format(zonedDateTime),
                minTemperature = dayItem.minTemperature.convertUnit(units.temperatureUnit).toString(),
                maxTemperature = dayItem.maxTemperature.convertUnit(units.temperatureUnit).toString(),
                weatherConditionIcons = dayItem.items.map { item -> item.weatherCondition.value.dayWeatherIcon },
                weatherConditions = dayItem.items.map { item -> item.weatherCondition.value.stringRes },
                precipitationProbabilities = dayItem.items.map { item ->
                    item.precipitationProbability.toString()
                }
            )
        }

        val nonPop = "-"
        displayPrecipitationProbability = items.any { it -> it.precipitationProbabilities.any { it != nonPop } }
    }


    data class Item(
        val id: Int,
        val date: String, val dayOfWeek: String, val minTemperature: String, val maxTemperature: String,
        val weatherConditionIcons: List<Int>,
        val weatherConditions: List<Int>,
        val precipitationProbabilities: List<String>,
    )
}