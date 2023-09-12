package io.github.pknujsp.weatherwizard.core.model.airquality

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityValueType
import java.time.ZonedDateTime

data class AirQualityEntity(
    val current: Current,
    val info: Info,
    val dailyForecast: DailyForecast,
) : EntityModel {

    data class Current(
        val aqi: AirQualityValueType,
        val co: AirQualityValueType,
        val no2: AirQualityValueType,
        val o3: AirQualityValueType,
        val pm10: AirQualityValueType,
        val pm25: AirQualityValueType,
        val so2: AirQualityValueType,
    )

    data class Info(
        val dataMeasurementTime: ZonedDateTime,
        val dataSourceName: String,
        val dataSourceWebsiteUrl: String,
        val stationLatitude: Double,
        val stationLongitude: Double,
        val stationName: String,
    )

    class DailyForecast(val items: List<Item>) {

        data class Item(
            val date: ZonedDateTime,
            val aqi: AirQualityValueType,
            val o3: Pollutant,
            val pm10: Pollutant,
            val pm25: Pollutant,
        ) {

            data class Pollutant(
                val aqi: AirQualityValueType,
                val avg: AirQualityValueType,
                val max: AirQualityValueType,
                val min: AirQualityValueType,
            )
        }
    }
}