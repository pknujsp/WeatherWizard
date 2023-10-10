package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CompareDailyForecast(
    items: List<DailyForecastEntity.Item>,
    units: CurrentUnits
) : UiModel {
    val items: List<Item> = items.let { items ->
        val listMap = mutableMapOf<String, Item>()
        var minTemp: TemperatureValueType
        var maxTemp: TemperatureValueType
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d\nE")

        items.forEachIndexed { i, item ->
            ZonedDateTime.parse(item.dateTime.value).format(dateFormatter).let { date ->
                if (!listMap.containsKey(date)) {
                    minTemp =
                        item.minTemperature.convertUnit(units.temperatureUnit)
                    maxTemp =
                        item.maxTemperature.convertUnit(units.temperatureUnit)

                    listMap[date] = Item(
                        id = i,
                        minTemperature = minTemp.toString(),
                        maxTemperature = maxTemp.toString(),
                    )
                }
                listMap[date]?.add(
                    item.weatherCondition, item.precipitationProbability)
            }
        }
        listMap.toList().map { it.second }
    }


    class Item(
        val id: Int, val minTemperature: String, val maxTemperature: String,
    ) {
        private val _weatherConditionIcons: MutableList<Int> = mutableListOf()
        private val _weatherConditions: MutableList<Int> = mutableListOf()
        private val _precipitationProbabilities: MutableList<String> = mutableListOf()

        val weatherConditionIcons: List<Int> = _weatherConditionIcons
        val weatherConditions: List<Int> = _weatherConditions
        val precipitationProbabilities: List<String> = _precipitationProbabilities

        fun add(weatherCondition: WeatherConditionValueType, precipitationProbability: ProbabilityValueType) {
            _weatherConditionIcons.add(weatherCondition.value.dayWeatherIcon)
            _weatherConditions.add(weatherCondition.value.stringRes)
            _precipitationProbabilities.add(precipitationProbability.toString())
        }
    }
}