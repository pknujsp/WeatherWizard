package io.github.pknujsp.weatherwizard.core.network.api.metnorway.response

import io.github.pknujsp.weatherwizard.core.model.weather.common.AmPm
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.MetNorwayResponse
import java.time.LocalDateTime

class MetNorwayDailyForecastResponse(
    metNorwayResponse: MetNorwayResponse, symbols: Map<String, WeatherConditionCategory>
) {

    val items: List<Item>

    private companion object {
        val sixTimes = listOf(0, 6, 12, 18)
    }

    init {
        val result = mutableListOf<Item>()
        // 기온, 풍향, 풍속, 강수량
        metNorwayResponse.properties.timeseries.groupBy { LocalDateTime.parse(it.time).toLocalDate() }.let { groups ->
            groups.entries.forEach { (date, items) ->
                var minTemp = Double.MAX_VALUE
                var maxTemp = Double.MIN_VALUE

                val firstTime = LocalDateTime.parse(items.first().time)

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
                        Item(amPm = amPmData.amPm,
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

    private fun LocalDateTime.isSkip(): Boolean = hour > 12

    private fun List<MetNorwayResponse.Properties.Timesery>.createAmPmDataList(symbols: Map<String, WeatherConditionCategory>): List<AmPmData> {
        val items = mutableListOf<AmPmData>()
        map { LocalDateTime.parse(it.time).hour to it }.let { pairs ->
            pairs.filter { it.first < 12 }.createAmPmData(AmPm.AM, symbols)?.run { items.add(this) }
            pairs.filter { it.first >= 12 }.createAmPmData(AmPm.PM, symbols)?.run { items.add(this) }
        }

        return items
    }

    private fun List<Pair<Int, MetNorwayResponse.Properties.Timesery>>.createAmPmData(
        amPm: AmPm, symbols: Map<String,
                WeatherConditionCategory>
    ):
            AmPmData? {
        return if (isNotEmpty()) {
            AmPmData(amPm, PrecipitationValueType(getPrecipitationVolume(), PrecipitationUnit.Millimeter),
                WeatherConditionValueType(getWeatherCondition(symbols)))
        } else {
            null
        }
    }

    private fun List<Pair<Int, MetNorwayResponse.Properties.Timesery>>.getPrecipitationVolume() =
        filter { it.first in sixTimes }.sumOf { it.second.data.next6Hours?.details?.precipitationAmount ?: 0.0 }

    private fun List<Pair<Int, MetNorwayResponse.Properties.Timesery>>.getWeatherCondition(symbols: Map<String, WeatherConditionCategory>) =
        groupBy { it.second.data.next1Hours?.summary?.symbolCode ?: it.second.data.next6Hours!!.summary.symbolCode }.let { groups ->
            groups.entries.maxBy { it.value.size }.let { symbols[it.key]!! }
        }

    data class Item(
        val amPm: AmPm,
        val dateTime: DateTimeValueType,
        val minTemperature: TemperatureValueType,
        val maxTemperature: TemperatureValueType,
        val windMinSpeed: WindSpeedValueType,
        val windMaxSpeed: WindSpeedValueType,
        val precipitationVolume: PrecipitationValueType,
        val weatherCondition: WeatherConditionValueType,
    )

    private data class AmPmData(
        val amPm: AmPm,
        val precipitationVolume: PrecipitationValueType,
        val weatherCondition: WeatherConditionValueType,
    )
}