package io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model

import io.github.pknujsp.everyweather.core.common.util.normalize
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DetailDailyForecast(
    dailyForecastEntity: DailyForecastEntity,
    units: CurrentUnits,
) : UiModel {
    val items: List<Item>
    val displayPrecipitationProbability: Boolean
    val displayPrecipitationVolume: Boolean

    init {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")
        val dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE")
        var zonedDateTime: ZonedDateTime
        var precipitationVolume: Double

        items = dailyForecastEntity.dayItems.mapIndexed { id, dayItem ->
            zonedDateTime = ZonedDateTime.parse(dayItem.dateTime.value)
            precipitationVolume = dayItem.items.filterNot { item ->
                item.precipitationVolume.isNone
            }.sumOf { item -> item.precipitationVolume.value }.normalize()

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
                },
                precipitationVolume = if (precipitationVolume > 0.0) PrecipitationValueType(precipitationVolume,
                    units.precipitationUnit).toString() else "",
            )
        }

        displayPrecipitationProbability = dailyForecastEntity.precipitationForecasted
        displayPrecipitationVolume = dailyForecastEntity.containsPrecipitationVolume
    }

    data class Item(
        val id: Int,
        val date: String,
        val dayOfWeek: String,
        val minTemperature: String,
        val maxTemperature: String,
        val weatherConditionIcons: List<Int>,
        val weatherConditions: List<Int>,
        val precipitationProbabilities: List<String>,
        val precipitationVolume: String = "",
    )
}