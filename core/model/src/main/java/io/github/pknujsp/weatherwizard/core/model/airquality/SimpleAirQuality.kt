package io.github.pknujsp.weatherwizard.core.model.airquality

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityValueType
import java.time.LocalDate

@Stable
data class SimpleAirQuality(
    val current: Current,
    val info: Info,
    val dailyForecast: List<DailyItem>,
) : UiModel {

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
        val dataMeasurementTime: String,
        val stationName: String,
    )

    @Stable
    data class DailyItem(
        val dateTime: LocalDate, val aqi: AirQualityValueType, val barHeightRatio: Float
    )
}