package io.github.pknujsp.everyweather.core.model.mock

import io.github.pknujsp.everyweather.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.everyweather.core.model.weather.common.HumidityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.PercentageUnit
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.RainfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.time.ZonedDateTime

open class MockDataGenerator {
    companion object {
        val fakeCurrentWeatherEntity by lazy {
            CurrentWeatherEntity(
                weatherCondition = WeatherConditionValueType(WeatherConditionCategory.Clear),
                temperature = TemperatureValueType(22, TemperatureUnit.default),
                feelsLikeTemperature = TemperatureValueType(21, TemperatureUnit.default),
                humidity = HumidityValueType(50, PercentageUnit),
                windSpeed = WindSpeedValueType(4.5, WindSpeedUnit.default),
                windDirection = WindDirectionValueType(80, WindDirectionUnit.Degree),
                precipitationVolume = PrecipitationValueType.none,
            )
        }

        val fakeHourlyForecastEntity by lazy {
            val now = ZonedDateTime.now()
            val weatherCondition = WeatherConditionValueType(WeatherConditionCategory.Clear)
            val temperature = TemperatureValueType(20, TemperatureUnit.default)
            val feelsLikeTemperature = TemperatureValueType(20, TemperatureUnit.default)
            val humidity = HumidityValueType(20, PercentageUnit)
            val windSpeed = WindSpeedValueType(4.5, WindSpeedUnit.default)
            val windDirection = WindDirectionValueType(80, WindDirectionUnit.Degree)

            val list =
                List(14) { index ->
                    HourlyForecastEntity.Item(
                        weatherCondition = weatherCondition,
                        temperature = temperature,
                        feelsLikeTemperature = feelsLikeTemperature,
                        humidity = humidity,
                        windSpeed = windSpeed,
                        windDirection = windDirection,
                        rainfallVolume = RainfallValueType.none,
                        snowfallVolume = SnowfallValueType.none,
                        precipitationVolume = PrecipitationValueType.none,
                        precipitationProbability = ProbabilityValueType.none,
                        dateTime = DateTimeValueType(now.plusHours(index.toLong()).toString()),
                    )
                }
            HourlyForecastEntity(list)
        }

        val fakeDailyForecastEntity by lazy {
            val now = ZonedDateTime.now()
            val dayItems =
                List(2) {
                    DailyForecastEntity.DayItem.Item(
                        weatherCondition = WeatherConditionValueType(WeatherConditionCategory.Clear),
                    )
                }
            val minTemperature = TemperatureValueType(17, TemperatureUnit.default)
            val maxTemperature = TemperatureValueType(20, TemperatureUnit.default)
            val windMinSpeed = WindSpeedValueType(3.0, WindSpeedUnit.default)
            val windMaxSpeed = WindSpeedValueType(6.5, WindSpeedUnit.default)

            val list =
                List(6) { index ->
                    DailyForecastEntity.DayItem(
                        dateTime = DateTimeValueType(now.plusDays(index.toLong()).toString()),
                        minTemperature = minTemperature,
                        maxTemperature = maxTemperature,
                        windMinSpeed = windMinSpeed,
                        windMaxSpeed = windMaxSpeed,
                        items = dayItems,
                    )
                }
            DailyForecastEntity(list)
        }
    }
}