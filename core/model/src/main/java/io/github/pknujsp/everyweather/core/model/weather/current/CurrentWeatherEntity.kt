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
        ## Current Weather
        
        - Weather condition : ${weatherCondition.value.description}
        - Temperature : $temperature
        - Feels like Temperature : $feelsLikeTemperature
        - Humidity : $humidity
        - Wind speed : $windSpeed
        - Wind direction : ${windDirection.convertUnit(WindDirectionUnit.Degree)}
        - Precipitation volume : $precipitationVolume
        
    """.trimIndent()


}