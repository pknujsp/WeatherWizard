package io.github.pknujsp.weatherwizard.core.model.weather.airquality

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.ContaminantConcentrationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeType

/**
 * @param co 일산화탄소 농도
 * @param no2 이산화질소 농도
 * @param o3 오존 농도
 * @param pm10 미세먼지(PM10) 농도
 * @param pm25 초미세먼지(PM2.5) 농도
 * @param so2 아황산가스 농도
 */
class CurrentAirQualityEntity(
    val co: ContaminantConcentrationType,
    val no2: ContaminantConcentrationType,
    val o3: ContaminantConcentrationType,
    val pm10: ContaminantConcentrationType,
    val pm25: ContaminantConcentrationType,
    val so2: ContaminantConcentrationType,
    val station: StationEntity,
    val dateTime:DateTimeType,

) : EntityModel {
    val hasAirQuality: Boolean
        get() = !co.isEmpty() or !no2.isEmpty() or !o3.isEmpty() or !pm10.isEmpty() or !pm25.isEmpty() or !so2.isEmpty()

    val hasCo = !co.isEmpty()
    val hasNo2 = !no2.isEmpty()
    val hasO3 = !o3.isEmpty()
    val hasPm10 = !pm10.isEmpty()
    val hasPm25 = !pm25.isEmpty()
    val hasSo2 = !so2.isEmpty()
}