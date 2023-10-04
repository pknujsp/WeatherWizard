package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DetailDailyForecast(
    dailyForecastEntity: DailyForecastEntity,
    units: CurrentUnits
) : UiModel {
    val items: List<Item>
    val displayPrecipitationProbability: Boolean

    init {
        items = dailyForecastEntity.items.let { items ->
            val listMap = mutableMapOf<String, Item>()
            var minTemp: TemperatureValueType
            var maxTemp: TemperatureValueType

            val dateFormatter = DateTimeFormatter.ofPattern("M.d EEE")

            items.forEachIndexed { i, item ->
                ZonedDateTime.parse(item.dateTime.value).format(dateFormatter).let { date ->
                    if (!listMap.containsKey(date)) {
                        minTemp =
                            TemperatureValueType(item.minTemperature.convertUnit(units.temperatureUnit), units
                                .temperatureUnit)
                        maxTemp =
                            TemperatureValueType(item.maxTemperature.convertUnit(units.temperatureUnit), units
                                .temperatureUnit)

                        val splittedDates = date.split(" ")
                        listMap[date] = Item(
                            id = i,
                            date = splittedDates[0],
                            dayOfWeek = splittedDates[1],
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

        val nonPop = "-"
        displayPrecipitationProbability = items.any { it -> it.precipitationProbabilities.any { it != nonPop } }
    }


    class Item(
        val id: Int,
        val date: String, val dayOfWeek: String, val minTemperature: String, val maxTemperature: String,
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