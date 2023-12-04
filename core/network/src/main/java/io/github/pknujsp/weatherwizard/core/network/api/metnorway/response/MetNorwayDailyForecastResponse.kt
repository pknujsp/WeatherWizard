package io.github.pknujsp.weatherwizard.core.network.api.metnorway.response

import io.github.pknujsp.weatherwizard.core.model.weather.common.TimeOfDayType
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.weatherwizard.core.network.api.DailyForecastResponseModel
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.MetNorwayResponse
import java.time.ZonedDateTime

class MetNorwayDailyForecastResponse(
    metNorwayResponse: MetNorwayResponse, symbols: Map<String, WeatherConditionCategory>
) : DailyForecastResponseModel {

    val items: List<Item>

    private companion object {
        val sixTimes = listOf(0, 6, 12, 18)
        val night = "_night"
        val day = "_day"
    }

    init {
        val result = mutableListOf<Item>()
        // 기온, 풍향, 풍속, 강수량
        metNorwayResponse.properties.timeseries.groupBy { ZonedDateTime.parse(it.time).toLocalDate() }.let { groups ->
            for ((i, entry) in groups.entries.withIndex()) {
                if (i == groups.size - 1) continue

                val date = entry.key
                val items = entry.value
                var minTemp = Double.MAX_VALUE
                var maxTemp = Double.MIN_VALUE

                val firstTime = ZonedDateTime.parse(items.first().time)

                items.forEach { item ->
                    item.data.instant.details.airTemperature.run {
                        minTemp = minOf(minTemp, this)
                        maxTemp = maxOf(maxTemp, this)
                    }
                    item.data.next6Hours?.run {
                        minTemp = minOf(minTemp, details.airTemperatureMin)
                        maxTemp = maxOf(maxTemp, details.airTemperatureMax)
                    }
                }

                val minWindSpeed = items.minOf { it.data.instant.details.windSpeed }
                val maxWindSpeed = items.maxOf { it.data.instant.details.windSpeed }
                items.createAmPmDataList(symbols).forEach { amPmData ->
                    result.add(
                        Item(timeOfDayType = amPmData.timeOfDayType,
                            dateTime = DateTimeValueType(firstTime.toString()),
                            minTemperature = TemperatureValueType(minTemp, TemperatureUnit.Celsius),
                            maxTemperature = TemperatureValueType(maxTemp, TemperatureUnit.Celsius),
                            windMinSpeed = WindSpeedValueType(value = minWindSpeed, unit = WindSpeedUnit.MeterPerSecond),
                            windMaxSpeed = WindSpeedValueType(value = maxWindSpeed, unit = WindSpeedUnit.MeterPerSecond),
                            precipitationVolume = amPmData.precipitationVolume,
                            weatherCondition = amPmData.weatherCondition
                        )
                    )
                }
            }
        }

        items = result
    }


    private fun List<MetNorwayResponse.Properties.Timesery>.createAmPmDataList(symbols: Map<String, WeatherConditionCategory>): List<AmPmData> {
        val items = mutableListOf<AmPmData>()
        map { ZonedDateTime.parse(it.time).hour to it }.let { pairs ->
            pairs.filter { it.first < 12 }.createAmPmData(TimeOfDayType.AM, symbols)?.run { items.add(this) }
            pairs.filter { it.first >= 12 }.createAmPmData(TimeOfDayType.PM, symbols)?.run { items.add(this) }
        }

        return items
    }

    private fun List<Pair<Int, MetNorwayResponse.Properties.Timesery>>.createAmPmData(
        timeOfDayType: TimeOfDayType, symbols: Map<String,
                WeatherConditionCategory>
    ):
            AmPmData? {
        return if (isNotEmpty()) {
            AmPmData(timeOfDayType, PrecipitationValueType(getPrecipitationVolume(), PrecipitationUnit.Millimeter),
                WeatherConditionValueType(getWeatherCondition(symbols)))
        } else {
            null
        }
    }

    private fun List<Pair<Int, MetNorwayResponse.Properties.Timesery>>.getPrecipitationVolume() =
        filter { it.first in sixTimes }.sumOf { it.second.data.next6Hours?.details?.precipitationAmount ?: 0.0 }

    private fun List<Pair<Int, MetNorwayResponse.Properties.Timesery>>.getWeatherCondition(symbols: Map<String, WeatherConditionCategory>) =
        groupBy { it.second.data.next1Hours?.summary?.symbolCode ?: it.second.data.next6Hours!!.summary.symbolCode }.let { groups ->
            groups.entries.maxBy { it.value.size }.let { symbols[it.key.replace(night, "").replace(day, "")]!! }
        }

    data class Item(
        val timeOfDayType: TimeOfDayType,
        val dateTime: DateTimeValueType,
        val minTemperature: TemperatureValueType,
        val maxTemperature: TemperatureValueType,
        val windMinSpeed: WindSpeedValueType,
        val windMaxSpeed: WindSpeedValueType,
        val precipitationVolume: PrecipitationValueType,
        val weatherCondition: WeatherConditionValueType,
    )

    private data class AmPmData(
        val timeOfDayType: TimeOfDayType,
        val precipitationVolume: PrecipitationValueType,
        val weatherCondition: WeatherConditionValueType,
    )
}