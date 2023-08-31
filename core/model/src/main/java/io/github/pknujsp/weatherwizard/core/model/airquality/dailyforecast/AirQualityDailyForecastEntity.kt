package io.github.pknujsp.weatherwizard.core.model.airquality.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.EntityModel

data class AirQualityDailyForecastEntity(
    val items: List<Item>
) : EntityModel {
    data class Item(
        val dateTime: String
    )
}