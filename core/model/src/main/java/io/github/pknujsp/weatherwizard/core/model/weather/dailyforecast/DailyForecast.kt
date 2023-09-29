package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DailyForecast(
    dailyForecastEntity: DailyForecastEntity,
) : UiModel {
    val items: List<Item>
    val displayPrecipitationProbability: Boolean

    companion object {
        val itemWidth: Dp = 92.dp
        val temperatureGraphHeight: Dp = 50.dp
        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d\nE")

        @DrawableRes val probabilityIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.pop
        @DrawableRes val rainfallIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.raindrop
        @DrawableRes val snowfallIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.snowparticle
    }

    init {
        items = dailyForecastEntity.items.let { items ->
            val listMap = mutableMapOf<String, Item>()
            items.forEachIndexed { i, item ->
                ZonedDateTime.parse(item.dateTime.value).format(dateFormatter).let { date ->
                    if (!listMap.containsKey(date)) {
                        listMap[date] = Item(
                            id = i,
                            date = date,
                            minTemperature = item.minTemperature.toString(),
                            maxTemperature = item.maxTemperature.toString(),
                            minTemperatureInt = item.minTemperature.value.toInt(),
                            maxTemperatureInt = item.maxTemperature.value.toInt(),
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
        val date: String, val minTemperature: String, val maxTemperature: String,
        val minTemperatureInt: Int, val maxTemperatureInt: Int
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