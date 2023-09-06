package io.github.pknujsp.weatherwizard.core.model.weather.airquality

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType

data class DailyForecastAirQualityEntity(
    val items: List<Item>,
) : EntityModel {

    data class Item(
        val dataTime: DateTimeValueType,
        val pm10: Detail,
        val pm25: Detail,
        val o3: Detail,
    ) {
        val hasAirQuality: Boolean = pm10.hasAirQuality or pm25.hasAirQuality or o3.hasAirQuality

        data class Detail(
            val min: Int,
            val max: Int,
            val avg: Int,
        ) {
            val hasAirQuality: Boolean = min != -1 && max != -1 && avg != -1
        }

    }
}