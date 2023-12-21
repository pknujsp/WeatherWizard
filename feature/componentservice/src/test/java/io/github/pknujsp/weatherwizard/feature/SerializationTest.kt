package io.github.pknujsp.weatherwizard.feature

import io.github.pknujsp.weatherwizard.core.common.module.UtilsModule
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
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
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.worker.model.WidgetRemoteViewUiState
import org.junit.Assert
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
        Assert.assertTrue(currentWeatherEntity == parsedWeatherEntity)
        Assert.assertTrue(hourlyForecastEntity == parsedHourlyForecast)

        val state = WidgetRemoteViewUiState(widget = WidgetSettingsEntity(1, widgetType = WidgetType.ALL_IN_ONE),
            lastUpdated = null,
            isSuccessful = true,
            address = "address",
            model = WeatherResponseEntity(
                weatherDataMajorCategories = WidgetType.ALL_IN_ONE.categories.toSet(),
                responses = listOf(currentWeatherEntity, hourlyForecastEntity),
                dayNightCalculator = DayNightCalculator(0.0, 0.0),
            ))

        val dbModel = state.toWidgetResponseDBModel(jsonParser)
        dbModel.entities
    }

}