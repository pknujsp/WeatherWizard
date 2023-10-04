package io.github.pknujsp.weatherwizard.core.data.weather.metnorway

import io.github.pknujsp.weatherwizard.core.data.weather.DefaultValueUnit
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapper
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
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaYesterdayWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayHourlyForecastResponse
import javax.inject.Inject


class MetNorwayResponseMapper @Inject constructor() :
    WeatherResponseMapper<MetNorwayCurrentWeatherResponse, MetNorwayHourlyForecastResponse, MetNorwayDailyForecastResponse, KmaYesterdayWeatherResponse> {

    private companion object : DefaultValueUnit {
        override val DEFAULT_TEMPERATURE_UNIT = TemperatureUnit.Celsius
        override val DEFAULT_WIND_SPEED_UNIT = WindSpeedUnit.MeterPerSecond
        override val DEFAULT_WIND_DIRECTION_UNIT = WindDirectionUnit.Degree
        override val DEFAULT_PRECIPITATION_UNIT = PrecipitationUnit.Millimeter
        override val DEFAULT_VISIBILITY_UNIT = VisibilityUnit.Kilometer
        override val DEFAULT_PRESSURE_UNIT = PressureUnit.Hectopascal
    }

    override fun mapCurrentWeather(response: MetNorwayCurrentWeatherResponse): CurrentWeatherEntity {
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

    override fun mapHourlyForecast(response: MetNorwayHourlyForecastResponse): HourlyForecastEntity {
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

    override fun mapDailyForecast(response: MetNorwayDailyForecastResponse): DailyForecastEntity {
        return DailyForecastEntity(response.items.map { item ->
            DailyForecastEntity.Item(
                dateTime = item.dateTime,
                weatherCondition = item.weatherCondition,
                minTemperature = item.minTemperature,
                maxTemperature = item.maxTemperature,
                rainfallVolume = RainfallValueType.none,
                snowfallVolume = SnowfallValueType.none,
                rainfallProbability = ProbabilityValueType.none,
                snowfallProbability = ProbabilityValueType.none,
                precipitationVolume = item.precipitationVolume,
                precipitationProbability = ProbabilityValueType.none,
                windMinSpeed = item.windMinSpeed,
                windMaxSpeed = item.windMaxSpeed,
            )
        })
    }

    override fun mapYesterdayWeather(response: KmaYesterdayWeatherResponse): YesterdayWeatherEntity {
        TODO("어제 날씨 정보 없음")
    }
}