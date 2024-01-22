package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory

class CompareDailyForecast(
    dayItems: List<DailyForecastEntity.DayItem>,
    units: CurrentUnits
) : UiModel {
    val items: List<Item> = dayItems.mapIndexed { id, dayItem ->
        Item(
            id = id,
            minTemperature = dayItem.minTemperature.convertUnit(units.temperatureUnit).toString(),
            maxTemperature = dayItem.maxTemperature.convertUnit(units.temperatureUnit).toString(),
            weatherConditions = dayItem.items.map { it.weatherCondition.value },
        )
    }


    data class Item(
        val id: Int, val minTemperature: String, val maxTemperature: String,
        val weatherConditions: List<WeatherConditionCategory>,
    )
}