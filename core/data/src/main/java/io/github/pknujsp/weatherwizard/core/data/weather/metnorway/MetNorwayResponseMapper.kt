package io.github.pknujsp.weatherwizard.core.data.weather.metnorway

import io.github.pknujsp.weatherwizard.core.data.weather.DefaultValueUnit
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapper
import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PressureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.RainfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.VisibilityUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayHourlyForecastResponse


internal class MetNorwayResponseMapper : WeatherResponseMapper<WeatherEntityModel> {

    private companion object : DefaultValueUnit {
        override val DEFAULT_TEMPERATURE_UNIT = TemperatureUnit.Celsius
        override val DEFAULT_WIND_SPEED_UNIT = WindSpeedUnit.MeterPerSecond
        override val DEFAULT_WIND_DIRECTION_UNIT = WindDirectionUnit.Degree
        override val DEFAULT_PRECIPITATION_UNIT = PrecipitationUnit.Millimeter
        override val DEFAULT_VISIBILITY_UNIT = VisibilityUnit.Kilometer
        override val DEFAULT_PRESSURE_UNIT = PressureUnit.Hectopascal
    }

    private fun mapCurrentWeather(response: MetNorwayCurrentWeatherResponse): CurrentWeatherEntity {
        return response.run {
            CurrentWeatherEntity(
                weatherCondition = weatherCondition,
                temperature = temperature,
                feelsLikeTemperature = feelsLikeTemperature,
                humidity = humidity,
                windDirection = windDirection,
                windSpeed = windSpeed,
                precipitationVolume = precipitationVolume,
            )
        }
    }

    private fun mapHourlyForecast(response: MetNorwayHourlyForecastResponse): HourlyForecastEntity {
        return HourlyForecastEntity(response.items.map { item ->
            HourlyForecastEntity.Item(
                dateTime = item.dateTime,
                weatherCondition = item.weatherCondition,
                temperature = item.temperature,
                humidity = item.humidity,
                windSpeed = item.windSpeed,
                windDirection = item.windDirection,
                feelsLikeTemperature = item.feelsLikeTemperature,
                rainfallVolume = RainfallValueType.none,
                snowfallVolume = SnowfallValueType.none,
                precipitationVolume = item.precipitationVolume,
                precipitationProbability = ProbabilityValueType.none,
            )
        })
    }

    private fun mapDailyForecast(response: MetNorwayDailyForecastResponse): DailyForecastEntity {
        val dayItems = response.items.groupBy { it.dateTime }.map { (day, dayItems) ->
            DailyForecastEntity.DayItem(dateTime = day,
                minTemperature = dayItems[0].minTemperature,
                maxTemperature = dayItems[0].maxTemperature,
                windMinSpeed = dayItems[0].windMinSpeed,
                windMaxSpeed = dayItems[0].windMaxSpeed,
                items = dayItems.map { item ->
                    DailyForecastEntity.DayItem.Item(
                        weatherCondition = item.weatherCondition,
                        precipitationVolume = item.precipitationVolume,
                    )
                })
        }
        return DailyForecastEntity(dayItems)
    }

    override fun map(apiResponseModel: ApiResponseModel, majorWeatherEntityType: MajorWeatherEntityType) = when (majorWeatherEntityType) {
        MajorWeatherEntityType.CURRENT_CONDITION -> mapCurrentWeather(apiResponseModel as MetNorwayCurrentWeatherResponse)
        MajorWeatherEntityType.HOURLY_FORECAST -> mapHourlyForecast(apiResponseModel as MetNorwayHourlyForecastResponse)
        MajorWeatherEntityType.DAILY_FORECAST -> mapDailyForecast(apiResponseModel as MetNorwayDailyForecastResponse)
        else -> throw IllegalArgumentException("Invalid majorWeatherEntityType: $majorWeatherEntityType")
    }
}