package io.github.pknujsp.weatherwizard.model

import io.github.pknujsp.weatherwizard.core.common.module.UtilsModule
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PercentageUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.RainfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.ZonedDateTime

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SerializationTest {

    val jsonParser = JsonParser(UtilsModule.providesKtJson())

    @Test
    fun weather_entity_to_bytearray() {
        val currentWeatherEntity = CurrentWeatherEntity(weatherCondition = WeatherConditionValueType(WeatherConditionCategory.Clear),
            temperature = TemperatureValueType(20.0, TemperatureUnit.default),
            feelsLikeTemperature = TemperatureValueType(20.0, TemperatureUnit.default),
            humidity = HumidityValueType(20, PercentageUnit),
            windSpeed = WindSpeedValueType.none,
            windDirection = WindDirectionValueType.none,
            precipitationVolume = PrecipitationValueType.snowDrop)

        val hourlyForecastEntity =
            HourlyForecastEntity(listOf(HourlyForecastEntity.Item(weatherCondition = WeatherConditionValueType(WeatherConditionCategory.Clear),
                temperature = TemperatureValueType(20.0, TemperatureUnit.default),
                feelsLikeTemperature = TemperatureValueType(20.0, TemperatureUnit.default),
                humidity = HumidityValueType(20, PercentageUnit),
                windSpeed = WindSpeedValueType.none,
                windDirection = WindDirectionValueType.none,
                rainfallVolume = RainfallValueType.none,
                snowfallVolume = SnowfallValueType.none,
                precipitationVolume = PrecipitationValueType.none,
                precipitationProbability = ProbabilityValueType.none,
                dateTime = DateTimeValueType(ZonedDateTime.now().toString())),
                HourlyForecastEntity.Item(weatherCondition = WeatherConditionValueType(WeatherConditionCategory.Clear),
                    temperature = TemperatureValueType(20.0, TemperatureUnit.default),
                    feelsLikeTemperature = TemperatureValueType(20.0, TemperatureUnit.default),
                    humidity = HumidityValueType(20, PercentageUnit),
                    windSpeed = WindSpeedValueType.none,
                    windDirection = WindDirectionValueType.none,
                    rainfallVolume = RainfallValueType.none,
                    snowfallVolume = SnowfallValueType.none,
                    precipitationVolume = PrecipitationValueType.none,
                    precipitationProbability = ProbabilityValueType.none,
                    dateTime = DateTimeValueType(ZonedDateTime.now().toString()))))

        val byteArray = jsonParser.parseToByteArray(currentWeatherEntity)
        val byteArray2 = jsonParser.parseToByteArray(hourlyForecastEntity)
        val parsedWeatherEntity: CurrentWeatherEntity = jsonParser.parse(byteArray)
        val parsedHourlyForecast: HourlyForecastEntity = jsonParser.parse(byteArray2)
        assertTrue(currentWeatherEntity == parsedWeatherEntity)
        assertTrue(hourlyForecastEntity == parsedHourlyForecast)
    }

}