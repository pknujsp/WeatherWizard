package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits

class CompareDailyForecast(
    dayItems: List<DailyForecastEntity.DayItem>,
    units: CurrentUnits
) : UiModel {
    val items: List<Item> = dayItems.mapIndexed { id, dayItem ->
        Item(
            id = id,
            minTemperature = dayItem.minTemperature.convertUnit(units.temperatureUnit).toString(),
            maxTemperature = dayItem.maxTemperature.convertUnit(units.temperatureUnit).toString(),
            weatherConditionIcons = dayItem.items.map { item -> item.weatherCondition.value.dayWeatherIcon },
            weatherConditions = dayItem.items.map { item -> item.weatherCondition.value.stringRes }
        )
    }


    data class Item(
        val id: Int, val minTemperature: String, val maxTemperature: String,
        val weatherConditionIcons: List<Int>,
        val weatherConditions: List<Int>
    )
}