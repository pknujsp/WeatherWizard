package io.github.pknujsp.everyweather.core.model.weather.current

import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.HumidityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedValueType
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherEntity(
    val weatherCondition: WeatherConditionValueType,
    val temperature: TemperatureValueType,
    val feelsLikeTemperature: TemperatureValueType,
    val humidity: HumidityValueType,
    val windSpeed: WindSpeedValueType,
    val windDirection: WindDirectionValueType,
    val precipitationVolume: PrecipitationValueType,
) : WeatherEntityModel() {


    override fun toString(): String = """
        ## 현재 날씨
        - 날씨 : ${weatherCondition.value.description}
        - 기온 : $temperature
        - 체감 온도 : $feelsLikeTemperature
        - 습도 : $humidity
        - 풍속 : $windSpeed
        - 풍향 : ${windDirection.convertUnit(WindDirectionUnit.Degree)}
        - 강수량 : $precipitationVolume
    """.trimIndent()


}