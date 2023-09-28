package io.github.pknujsp.weatherwizard.core.network.api.metnorway.response

import io.github.pknujsp.weatherwizard.core.common.util.WeatherUtil
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
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.MetNorwayResponse
import java.time.ZoneId
import java.time.ZonedDateTime

class MetNorwayHourlyForecastResponse(
    metNorwayResponse: MetNorwayResponse,
    weatherUtil: WeatherUtil,
    symbols: Map<String, WeatherConditionCategory>
) {
    val items: List<Item> = metNorwayResponse.properties.timeseries.let { timeseries ->
        val zero = 0.0

        var precipitationVolume: Double = 0.0
        var hasPrecipitation = false

        val zoneId = ZoneId.systemDefault()
        var windSpeed: WindSpeedValueType
        var weatherCondition: WeatherConditionValueType? = null

        timeseries.filter { it.data.next1Hours != null && it.data.next6Hours != null }.map { hourly ->
            hourly.data.instant.details.let { instantDetails ->
                windSpeed = WindSpeedValueType(
                    instantDetails.windSpeed,
                    WindSpeedUnit.MeterPerSecond
                )

                (hourly.data.next1Hours?.details?.precipitationAmount
                    ?: hourly.data.next6Hours?.details?.precipitationAmount)?.let { precipitation ->
                    precipitationVolume = precipitation
                    hasPrecipitation = precipitation != zero
                }

                (hourly.data.next1Hours?.summary?.symbolCode ?: hourly.data.next6Hours?.summary?.symbolCode)?.let { symbolCode ->
                    weatherCondition = WeatherConditionValueType(symbols[symbolCode]!!)
                }

                Item(
                    dateTime = DateTimeValueType(ZonedDateTime.parse(hourly.time).withZoneSameInstant(zoneId).withMinute(0).withSecond
                        (0).withNano(0).toString()),
                    temperature = TemperatureValueType(instantDetails.airTemperature, TemperatureUnit.Celsius),
                    feelsLikeTemperature = TemperatureValueType(weatherUtil.calcFeelsLikeTemperature(instantDetails.airTemperature,
                        windSpeed.convertUnit(windSpeed.value, WindSpeedUnit.KilometerPerHour),
                        instantDetails.relativeHumidity), TemperatureUnit.Celsius),
                    humidity = HumidityValueType(instantDetails.relativeHumidity.toInt(), PercentageUnit),
                    windDirection = WindDirectionValueType(instantDetails.windFromDirection.toInt(), WindDirectionUnit.Degree),
                    windSpeed = windSpeed,
                    precipitationVolume = PrecipitationValueType(precipitationVolume, PrecipitationUnit.Millimeter),
                    weatherCondition = weatherCondition!!,
                    dewPointTemperature = TemperatureValueType(instantDetails.dewPointTemperature, TemperatureUnit.Celsius),
                    airPressure = PressureValueType(instantDetails.airPressureAtSeaLevel.toInt(), PressureUnit.Hectopascal),
                )

            }
        }

    }

    data class Item(
        val dateTime: DateTimeValueType,
        val temperature: TemperatureValueType,
        val feelsLikeTemperature: TemperatureValueType,
        val humidity: HumidityValueType,
        val windDirection: WindDirectionValueType,
        val windSpeed: WindSpeedValueType,
        val precipitationVolume: PrecipitationValueType,
        val weatherCondition: WeatherConditionValueType,
        val dewPointTemperature: TemperatureValueType,
        val airPressure: PressureValueType,
    )
}