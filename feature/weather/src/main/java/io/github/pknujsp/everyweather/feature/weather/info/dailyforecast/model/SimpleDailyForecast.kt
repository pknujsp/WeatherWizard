package io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SimpleDailyForecast(
    dailyForecastEntity: DailyForecastEntity,
    units: CurrentUnits
) : UiModel {
    val items: List<Item>
    val displayPrecipitationProbability: Boolean

    companion object {
        val itemWidth: Dp = 92.dp
        val temperatureGraphHeight: Dp = 52.dp
        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d\nE")

        @DrawableRes val probabilityIcon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_umbrella
    }

    init {
        var minTemperature: TemperatureValueType
        var maxTemperature: TemperatureValueType
        items = dailyForecastEntity.dayItems.mapIndexed { id, dayItem ->
            minTemperature = dayItem.minTemperature.convertUnit(units.temperatureUnit)
            maxTemperature = dayItem.maxTemperature.convertUnit(units.temperatureUnit)
            Item(
                id = id,
                date = dateFormatter.format(ZonedDateTime.parse(dayItem.dateTime.value)),
                minTemperature = minTemperature.toString(),
                maxTemperature = maxTemperature.toString(),
                minTemperatureInt = minTemperature.value.toInt(),
                maxTemperatureInt = maxTemperature.value.toInt(),
                weatherConditionIcons = dayItem.items.map { item -> item.weatherCondition.value.dayWeatherIcon },
                weatherConditions = dayItem.items.map { item -> item.weatherCondition.value.stringRes },
                precipitationProbabilities = dayItem.items.map { item ->
                    item.precipitationProbability.toString()
                }
            )
        }
        val nonPop = "-"
        displayPrecipitationProbability = items.any { it -> it.precipitationProbabilities.any { it != nonPop } }
    }


    data class Item(
        val id: Int,
        val date: String, val minTemperature: String, val maxTemperature: String,
        val minTemperatureInt: Int, val maxTemperatureInt: Int,
        val weatherConditionIcons: List<Int>,
        val weatherConditions: List<Int>,
        val precipitationProbabilities: List<String>,
    )
}