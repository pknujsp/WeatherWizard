package io.github.pknujsp.weatherwizard.core.model.airquality

import io.github.pknujsp.weatherwizard.core.model.VarState
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityValueType
import java.time.LocalDate
import java.time.ZonedDateTime

data class AirQualityEntity(
    val current: Current,
    val info: Info,
    val dailyForecast: DailyForecast,
) : WeatherEntityModel() {

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
            val date: LocalDate,
            val o3: VarState<Pollutant> = VarState.Uninitialized,
            val pm10: VarState<Pollutant> = VarState.Uninitialized,
            val pm25: VarState<Pollutant> = VarState.Uninitialized,
        ) {
            val aqi: VarState<AirQualityValueType>

            init {
                val avg =
                    listOf(o3, pm10, pm25).filterIsInstance<VarState.Initialized<Pollutant>>().map { it.data.avg.value }.average().toInt()
                aqi = VarState.Initialized(AirQualityValueType(value = avg, airQualityDescription = AirQualityDescription.fromValue(avg)))
            }

            data class Pollutant(
                val avg: AirQualityValueType,
                val max: AirQualityValueType,
                val min: AirQualityValueType,
            ) {
                val aqi: AirQualityValueType = avg
            }
        }
    }
}