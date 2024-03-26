package io.github.pknujsp.everyweather.feature.weather.info.dailyforecast

import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.common.util.normalize
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.feature.weather.info.ForecastModelMapper
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.SimpleDailyForecast
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DailyForecastModelMapper : ForecastModelMapper<DailyForecastEntity, SimpleDailyForecast> {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d\nE")

    override fun mapTo(src: DailyForecastEntity, units: CurrentUnits, dayNightCalculator: DayNightCalculator): SimpleDailyForecast {
        var minTemperature: TemperatureValueType
        var maxTemperature: TemperatureValueType
        var precipitationVolume: Double

        val items = src.dayItems.mapIndexed { id, dayItem ->
            minTemperature = dayItem.minTemperature.convertUnit(units.temperatureUnit)
            maxTemperature = dayItem.maxTemperature.convertUnit(units.temperatureUnit)
            precipitationVolume = dayItem.items.filterNot { item ->
                item.precipitationVolume.isNone
            }.sumOf { item -> item.precipitationVolume.value }.normalize()

            SimpleDailyForecast.Item(id = id,
                date = dateFormatter.format(ZonedDateTime.parse(dayItem.dateTime.value)),
                minTemperature = minTemperature.toString(),
                maxTemperature = maxTemperature.toString(),
                minTemperatureInt = minTemperature.value.toInt(),
                maxTemperatureInt = maxTemperature.value.toInt(),
                weatherConditionIcons = dayItem.items.map { item -> item.weatherCondition.value.dayWeatherIcon },
                weatherConditions = dayItem.items.map { item -> item.weatherCondition.value.stringRes },
                precipitationProbabilities = if (src.precipitationForecasted) dayItem.items.map { item ->
                    item.precipitationProbability.toString()
                } else emptyList(),
                precipitationVolume = if (precipitationVolume > 0.0) PrecipitationValueType(precipitationVolume,
                    units.precipitationUnit).toString() else "")
        }

        return SimpleDailyForecast(items = items,
            displayPrecipitationProbability = src.precipitationForecasted,
            displayPrecipitationVolume = src.containsPrecipitationVolume)
    }
}