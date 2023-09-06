package io.github.pknujsp.weatherwizard.core.model.weather.airquality

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType

/**
 * @param co 일산화탄소 농도
 * @param no2 이산화질소 농도
 * @param o3 오존 농도
 * @param pm10 미세먼지(PM10) 농도
 * @param pm25 초미세먼지(PM2.5) 농도
 * @param so2 아황산가스 농도
 */
class CurrentAirQualityEntity(
    val co: Int,
    val no2: Int,
    val o3: Int,
    val pm10: Int,
    val pm25: Int,
    val so2: Int,
    val station: StationEntity,
    val dateTime: DateTimeValueType,

    ) : EntityModel {

}