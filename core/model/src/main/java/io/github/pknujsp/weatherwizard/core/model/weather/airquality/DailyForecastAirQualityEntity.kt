package io.github.pknujsp.weatherwizard.core.model.weather.airquality

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.ContaminantConcentrationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeType

data class DailyForecastAirQualityEntity(
    val items: List<Item>,
) : EntityModel {

    data class Item(
        val dataTime: DateTimeType,
        val pm10: Detail,
        val pm25: Detail,
        val o3: Detail,
    ) {
        val hasAirQuality: Boolean
            get() = pm10.hasAirQuality or pm25.hasAirQuality or o3.hasAirQuality

        data class Detail(
            val min: ContaminantConcentrationType,
            val max: ContaminantConcentrationType,
            val avg: ContaminantConcentrationType,
        ) {
            val hasAirQuality: Boolean
                get() = !min.isEmpty() or !max.isEmpty() or !avg.isEmpty()
        }

    }
}