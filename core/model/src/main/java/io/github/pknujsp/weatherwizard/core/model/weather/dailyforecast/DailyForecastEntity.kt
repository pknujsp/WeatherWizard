package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.EntityModel

data class DailyForecastEntity(
    val items: List<Item>
) : EntityModel {

    data class Item(
        val dateTime: String
    )
}