package io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast

import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.common.util.toCalendar
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.everyweather.feature.weather.info.ForecastModelMapper
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.DetailHourlyForecast
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DetailHourlyForecastModelMapper : ForecastModelMapper<HourlyForecastEntity, DetailHourlyForecast> {
    private val formatter = DateTimeFormatter.ofPattern("M.d EEE")
    override fun mapTo(src: HourlyForecastEntity, units: CurrentUnits, dayNightCalculator: DayNightCalculator): DetailHourlyForecast {
        var keyId = 0

        val items = src.items.groupBy {
            ZonedDateTime.parse(it.dateTime.value).dayOfYear
        }.map { (_, items) ->
            DetailHourlyForecast.Header(
                id = keyId++,
                title = ZonedDateTime.parse(items.first().dateTime.value).format(formatter),
            ) to items.map {
                val dateTime = ZonedDateTime.parse(it.dateTime.value)

                DetailHourlyForecast.Item(
                    id = keyId++,
                    hour = dateTime.hour.toString(),
                    weatherCondition = it.weatherCondition.value.stringRes,
                    temperature = it.temperature.toString(),
                    precipitationVolume = it.precipitationVolume.convertUnit(units.precipitationUnit).toString(),
                    precipitationProbability = it.precipitationProbability.toString(),
                    weatherIcon = it.weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(dateTime.toCalendar()) == DayNightCalculator.DayNight.DAY),
                    rainfallVolume = it.rainfallVolume.convertUnit(units.precipitationUnit).toString(),
                    snowfallVolume = it.snowfallVolume.convertUnit(units.precipitationUnit).toString(),
                )
            }
        }

        var displayRainfallVolume = false
        var displaySnowfallVolume = false
        var displayPrecipitationVolume = false
        val displayPrecipitationProbability = src.precipitationForecasted

        if (displayPrecipitationProbability) {
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
            }
        }

        return DetailHourlyForecast(items = items,
            displayRainfallVolume = displayRainfallVolume,
            displaySnowfallVolume = displaySnowfallVolume,
            displayPrecipitationVolume = displayPrecipitationVolume,
            displayPrecipitationProbability = displayPrecipitationProbability)
    }
}