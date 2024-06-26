package io.github.pknujsp.everyweather.core.network.api.metnorway.response

import io.github.pknujsp.everyweather.core.common.util.FeelsLikeTemperatureCalculator
import io.github.pknujsp.everyweather.core.common.util.normalize
import io.github.pknujsp.everyweather.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.everyweather.core.model.weather.common.HumidityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.PercentageUnit
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.PressureUnit
import io.github.pknujsp.everyweather.core.model.weather.common.PressureValueType
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.everyweather.core.network.api.HourlyForecastResponseModel
import io.github.pknujsp.everyweather.core.network.api.metnorway.MetNorwayResponse
import java.time.ZoneId
import java.time.ZonedDateTime

class MetNorwayHourlyForecastResponse(
    metNorwayResponse: MetNorwayResponse,
    weatherUtil: FeelsLikeTemperatureCalculator,
    symbols: Map<String, WeatherConditionCategory>,
) : HourlyForecastResponseModel {

    private companion object {
        const val NIGHT = "_night"
        const val DAY = "_day"
        const val ZERO = 0.0
    }

    val items: List<Item> = metNorwayResponse.properties.timeseries.let { timeseries ->
        var precipitationVolume: Double

        val zoneId = ZoneId.systemDefault()
        var windSpeed: WindSpeedValueType
        var weatherCondition: WeatherConditionValueType? = null

        timeseries.filter { it.data.next1Hours != null || it.data.next6Hours != null }.map { hourly ->
            hourly.data.instant.details.let { instantDetails ->
                windSpeed = WindSpeedValueType(
                    instantDetails.windSpeed,
                    WindSpeedUnit.MeterPerSecond,
                )

                precipitationVolume =
                    (hourly.data.next1Hours?.details?.precipitationAmount ?: hourly.data.next6Hours?.details?.precipitationAmount) ?: ZERO
                precipitationVolume = precipitationVolume.normalize()

                (hourly.data.next1Hours?.summary?.symbolCode ?: hourly.data.next6Hours?.summary?.symbolCode)?.let { symbolCode ->
                    weatherCondition = WeatherConditionValueType(
                        symbols[symbolCode.replace(DAY, "").replace(NIGHT, "")]!!,
                    )
                }

                Item(
                    dateTime = DateTimeValueType(
                        ZonedDateTime.parse(hourly.time).withZoneSameInstant(zoneId).withMinute(0).withSecond(0).withNano(0).toString(),
                    ),
                    temperature = TemperatureValueType(
                        instantDetails.airTemperature.toInt().toShort(),
                        TemperatureUnit.Celsius,
                    ),
                    feelsLikeTemperature = TemperatureValueType(
                        weatherUtil.calculateFeelsLikeTemperature(
                            instantDetails.airTemperature.toInt().toShort(),
                            windSpeed.convertUnit(WindSpeedUnit.KilometerPerHour).value,
                            instantDetails.relativeHumidity,
                        ),
                        TemperatureUnit.Celsius,
                    ),
                    humidity = HumidityValueType(
                        instantDetails.relativeHumidity.toInt().toShort(),
                    ),
                    windDirection = WindDirectionValueType(
                        instantDetails.windFromDirection.toInt().toShort(),
                    ),
                    windSpeed = windSpeed,
                    precipitationVolume = if (precipitationVolume != ZERO) PrecipitationValueType(
                        precipitationVolume,
                        PrecipitationUnit.Millimeter,
                    ) else PrecipitationValueType.None,
                    weatherCondition = weatherCondition!!,
                    dewPointTemperature = TemperatureValueType(
                        instantDetails.dewPointTemperature.toInt().toShort(),
                        TemperatureUnit.Celsius,
                    ),
                    airPressure = PressureValueType(
                        instantDetails.airPressureAtSeaLevel.toInt().toShort(),
                        PressureUnit.Hectopascal,
                    ),
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