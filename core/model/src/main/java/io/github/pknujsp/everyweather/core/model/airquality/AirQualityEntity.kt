package io.github.pknujsp.everyweather.core.model.airquality

import io.github.pknujsp.everyweather.core.model.VarState
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.AirQualityValueType
import kotlinx.serialization.Serializable

@Serializable
data class AirQualityEntity(
    val current: Current,
    val info: Info,
    val dailyForecast: DailyForecast,
) : WeatherEntityModel() {

    override fun toString(): String {
        return StringBuilder().apply {
            appendLine("""Air Quality
                - 현재 대기질 상태와 향후 약 7일간의 대기질 예보입니다.
            """.trimMargin())
            appendLine("- Current : ${current.aqi.airQualityDescription.description}")
            appendLine("- Daily")
            appendLine("Date, Status")
            for (item in dailyForecast.items) {
                appendLine("${item.date}, ${item.getAqi().valueNotNull().airQualityDescription.description}")
            }
        }.toString()
    }

    @Serializable
    data class Current(
        val aqi: AirQualityValueType,
        val co: AirQualityValueType,
        val no2: AirQualityValueType,
        val o3: AirQualityValueType,
        val pm10: AirQualityValueType,
        val pm25: AirQualityValueType,
        val so2: AirQualityValueType,
    )

    @Serializable
    data class Info(
        val dataMeasurementTime: String,
        val dataSourceName: String,
        val dataSourceWebsiteUrl: String,
        val stationLatitude: Double,
        val stationLongitude: Double,
        val stationName: String,
    )

    @Serializable
    class DailyForecast(val items: List<Item>) {
        @Serializable
        data class Item(
            val date: String,
            val o3: VarState<Pollutant> = VarState.Uninitialized,
            val pm10: VarState<Pollutant> = VarState.Uninitialized,
            val pm25: VarState<Pollutant> = VarState.Uninitialized,
        ) {

            fun getAqi(): VarState<AirQualityValueType> {
                val avg =
                    listOf(o3, pm10, pm25).filterIsInstance<VarState.Initialized<Pollutant>>().map { it.data.avg.value }.average().toInt()
                return VarState.Initialized(AirQualityValueType(value = avg, airQualityDescription = AirQualityDescription.fromValue(avg)))
            }

            @Serializable
            data class Pollutant(
                val avg: AirQualityValueType, val max: AirQualityValueType, val min: AirQualityValueType, val aqi: AirQualityValueType = avg
            )
        }
    }
}