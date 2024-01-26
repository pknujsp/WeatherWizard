package io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.model

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity

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