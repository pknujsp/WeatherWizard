package io.github.pknujsp.weatherwizard.core.network.api.metnorway.response

import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PercentageUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PressureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PressureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.weatherwizard.core.network.api.CurrentWeatherResponseModel
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.MetNorwayResponse
import java.time.ZoneId
import java.time.ZonedDateTime

class MetNorwayCurrentWeatherResponse(
    metNorwayResponse: MetNorwayResponse,
    symbols: Map<String, WeatherConditionCategory>
) : CurrentWeatherResponseModel {
    val dateTime: DateTimeValueType
    val temperature: TemperatureValueType
    val feelsLikeTemperature: TemperatureValueType
    val humidity: HumidityValueType
    val windDirection: WindDirectionValueType
    val windSpeed: WindSpeedValueType
    val precipitationVolume: PrecipitationValueType
    val weatherCondition: WeatherConditionValueType
    val dewPointTemperature: TemperatureValueType
    val airPressure: PressureValueType

    init {
        metNorwayResponse.run {
            dateTime = DateTimeValueType(ZonedDateTime.parse(properties.timeseries[0].time)
                .withZoneSameInstant(ZoneId.systemDefault()).toString())
            temperature = TemperatureValueType(properties.timeseries[0].data.instant.details.airTemperature, TemperatureUnit.Celsius)
            feelsLikeTemperature = TemperatureValueType(properties.timeseries[0].data.instant.details.airTemperature,
                TemperatureUnit.Celsius)
            humidity = HumidityValueType(properties.timeseries[0].data.instant.details.relativeHumidity.toInt(), PercentageUnit)
            windDirection = WindDirectionValueType(properties.timeseries[0].data.instant.details.windFromDirection.toInt(),
                WindDirectionUnit.Degree).convertUnit(WindDirectionUnit.Compass)
            windSpeed = WindSpeedValueType(properties.timeseries[0].data.instant.details.windSpeed, WindSpeedUnit.MeterPerSecond)
            precipitationVolume = PrecipitationValueType(properties.timeseries[0].data.next1Hours!!.details.precipitationAmount,
                PrecipitationUnit.Millimeter)

            val day = "_day"
            val night = "_night"

            weatherCondition = WeatherConditionValueType(symbols[properties.timeseries[0].data.next1Hours!!.summary.symbolCode.replace(
                night, "").replace(day, "")]!!)
            dewPointTemperature = TemperatureValueType(properties.timeseries[0].data.instant.details.dewPointTemperature,
                TemperatureUnit.Celsius)
            airPressure = PressureValueType(properties.timeseries[0].data.instant.details.airPressureAtSeaLevel.toInt(),
                PressureUnit.Hectopascal)
        }
    }
}