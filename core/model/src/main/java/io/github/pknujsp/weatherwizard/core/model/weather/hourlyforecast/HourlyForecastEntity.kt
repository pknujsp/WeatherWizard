package io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast

import io.github.pknujsp.weatherwizard.core.model.EntityModel

data class HourlyForecastEntity(
    val items: List<Item>
) : EntityModel {
    data class Item(
        val dateTime: String
    )
}