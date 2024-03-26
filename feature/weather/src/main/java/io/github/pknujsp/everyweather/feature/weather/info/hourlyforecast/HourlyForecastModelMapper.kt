package io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast

import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.common.util.toCalendar
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.everyweather.feature.weather.info.ForecastModelMapper
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.SimpleHourlyForecast
import java.time.ZonedDateTime

object HourlyForecastModelMapper : ForecastModelMapper<HourlyForecastEntity, SimpleHourlyForecast> {

    private const val NON_POP = "-"

    override fun mapTo(src: HourlyForecastEntity, units: CurrentUnits, dayNightCalculator: DayNightCalculator): SimpleHourlyForecast {
        val times = src.items.map { ZonedDateTime.parse(it.dateTime.value) }
        val items = src.items.mapIndexed { i, it ->
            val dateTime = times[i]
            val temperature = it.temperature.convertUnit(units.temperatureUnit)

            SimpleHourlyForecast.Item(id = i,
                time = dateTime.hour.toString(),
                weatherCondition = it.weatherCondition.value.stringRes,
                temperature = temperature.toString(),
                precipitationVolume = it.precipitationVolume.convertUnit(units.precipitationUnit).toStringWithoutUnit(),
                precipitationProbability = it.precipitationProbability.toString(),
                weatherIcon = it.weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(dateTime.toCalendar()) == DayNightCalculator.DayNight.DAY),
                rainfallVolume = it.rainfallVolume.convertUnit(units.precipitationUnit).toStringWithoutUnit(),
                snowfallVolume = it.snowfallVolume.convertUnit(units.precipitationUnit).toStringWithoutUnit(),
                temperatureInt = temperature.value.toInt())
        }

        var displayRainfallVolume = false
        var displaySnowfallVolume = false
        var displayPrecipitationVolume = false
        var displayPrecipitationProbability = false

        src.items.forEach {
            if (!it.rainfallVolume.isNone) {
                displayRainfallVolume = true
            }
            if (!it.snowfallVolume.isNone) {
                displaySnowfallVolume = true
            }
            if (!it.precipitationVolume.isNone) {
                displayPrecipitationVolume = true
            }
            if (!it.precipitationProbability.isNone) {
                displayPrecipitationProbability = true
            }
        }

        return SimpleHourlyForecast(items = items,
            times = times,
            displayRainfallVolume = displayRainfallVolume,
            displaySnowfallVolume = displaySnowfallVolume,
            displayPrecipitationVolume = displayPrecipitationVolume,
            displayPrecipitationProbability = displayPrecipitationProbability)
    }
}