package io.github.pknujsp.everyweather.feature.weather.info.dailyforecast

import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.feature.weather.info.ForecastModelMapper
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.SimpleDailyForecast
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DailyForecastModelMapper : ForecastModelMapper<DailyForecastEntity, SimpleDailyForecast> {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d\nE")
    private const val NON_POP = "-"

    override fun mapTo(src: DailyForecastEntity, units: CurrentUnits, dayNightCalculator: DayNightCalculator): SimpleDailyForecast {
        var minTemperature: TemperatureValueType
        var maxTemperature: TemperatureValueType

        val items = src.dayItems.mapIndexed { id, dayItem ->
            minTemperature = dayItem.minTemperature.convertUnit(units.temperatureUnit)
            maxTemperature = dayItem.maxTemperature.convertUnit(units.temperatureUnit)
            SimpleDailyForecast.Item(
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
                },
            )
        }
        val displayPrecipitationProbability = items.any { it -> it.precipitationProbabilities.any { it != NON_POP } }

        return SimpleDailyForecast(items = items, displayPrecipitationProbability = displayPrecipitationProbability)
    }
}