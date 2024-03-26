package io.github.pknujsp.everyweather.core.data.weather.kma

import io.github.pknujsp.everyweather.core.common.util.toLeastZero
import io.github.pknujsp.everyweather.core.data.weather.DefaultValueUnit
import io.github.pknujsp.everyweather.core.data.weather.mapper.WeatherResponseMapper
import io.github.pknujsp.everyweather.core.model.ApiResponseModel
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.everyweather.core.model.weather.common.HumidityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.everyweather.core.model.weather.common.PercentageUnit
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.PressureUnit
import io.github.pknujsp.everyweather.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.RainfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.common.VisibilityUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.everyweather.core.network.api.kma.KmaCurrentWeatherResponse
import io.github.pknujsp.everyweather.core.network.api.kma.KmaDailyForecastResponse
import io.github.pknujsp.everyweather.core.network.api.kma.KmaHourlyForecastResponse
import io.github.pknujsp.everyweather.core.network.api.kma.KmaYesterdayWeatherResponse

internal class KmaResponseMapper : WeatherResponseMapper<WeatherEntityModel> {
    private companion object : DefaultValueUnit {
        val weatherConditionMap = mapOf(
            "맑음" to WeatherConditionCategory.Clear,
            "구름조금" to WeatherConditionCategory.PartlyCloudy,
            "구름많음" to WeatherConditionCategory.MostlyCloudy,
            "구름 많음" to WeatherConditionCategory.MostlyCloudy,
            "흐림" to WeatherConditionCategory.Overcast,
            "비" to WeatherConditionCategory.Rain,
            "비/눈" to WeatherConditionCategory.RainAndSnow,
            "눈" to WeatherConditionCategory.Snow,
            "소나기" to WeatherConditionCategory.Shower,
            "빗방울" to WeatherConditionCategory.Raindrop,
            "빗방울/눈날림" to WeatherConditionCategory.RaindropAndSnowBlizzard,
            "눈날림" to WeatherConditionCategory.SnowBlizzard,
            "구름많고 비" to WeatherConditionCategory.Rain,
            "구름많고 비/눈" to WeatherConditionCategory.RainAndSnow,
            "구름많고 눈" to WeatherConditionCategory.Snow,
            "구름많고 소나기" to WeatherConditionCategory.Shower,
            "흐리고 비" to WeatherConditionCategory.Rain,
            "흐리고 비/눈" to WeatherConditionCategory.RainAndSnow,
            "흐리고 눈" to WeatherConditionCategory.Snow,
            "흐리고 소나기" to WeatherConditionCategory.Shower,
        )

        override val DEFAULT_TEMPERATURE_UNIT = TemperatureUnit.Celsius
        override val DEFAULT_WIND_SPEED_UNIT = WindSpeedUnit.MeterPerSecond
        override val DEFAULT_WIND_DIRECTION_UNIT = WindDirectionUnit.Degree
        override val DEFAULT_PRECIPITATION_UNIT = PrecipitationUnit.Millimeter
        override val DEFAULT_VISIBILITY_UNIT = VisibilityUnit.Kilometer
        override val DEFAULT_PRESSURE_UNIT = PressureUnit.Hectopascal
    }

    private fun mapCurrentWeather(response: KmaCurrentWeatherResponse): CurrentWeatherEntity {
        return response.run {
            CurrentWeatherEntity(
                weatherCondition = WeatherConditionValueType(mapWeatherCondition(weatherCondition)),
                temperature = TemperatureValueType(temperature, DEFAULT_TEMPERATURE_UNIT),
                humidity = HumidityValueType(humidity.toShort(), PercentageUnit),
                windSpeed = WindSpeedValueType(windSpeed, DEFAULT_WIND_SPEED_UNIT),
                windDirection = WindDirectionValueType(windDirection.toShort(), DEFAULT_WIND_DIRECTION_UNIT),
                feelsLikeTemperature = TemperatureValueType(feelsLikeTemperature, DEFAULT_TEMPERATURE_UNIT),
                precipitationVolume = PrecipitationValueType(precipitationVolume, DEFAULT_PRECIPITATION_UNIT),
            )
        }
    }

    private fun mapHourlyForecast(response: KmaHourlyForecastResponse): HourlyForecastEntity {
        val list = response.items.map { item ->
            HourlyForecastEntity.Item(
                dateTime = DateTimeValueType(item.dateTime),
                weatherCondition = WeatherConditionValueType(mapWeatherCondition(item.weatherDescription)),
                temperature = TemperatureValueType(item.temp, DEFAULT_TEMPERATURE_UNIT),
                humidity = HumidityValueType(item.humidity.toShort(), PercentageUnit),
                windSpeed = WindSpeedValueType(item.windSpeed, DEFAULT_WIND_SPEED_UNIT),
                windDirection = WindDirectionValueType(
                    item.windDirection.toShort(),
                    DEFAULT_WIND_DIRECTION_UNIT,
                ),
                feelsLikeTemperature = TemperatureValueType(item.feelsLikeTemp, DEFAULT_TEMPERATURE_UNIT),
                rainfallVolume = RainfallValueType(item.rainVolume, DEFAULT_PRECIPITATION_UNIT),
                snowfallVolume = SnowfallValueType(item.snowVolume, DEFAULT_PRECIPITATION_UNIT),
                precipitationProbability = ProbabilityValueType(item.pop.toShort(), PercentageUnit),
            )
        }

        return HourlyForecastEntity(items = list)
    }

    private fun mapDailyForecast(response: KmaDailyForecastResponse): DailyForecastEntity {
        val dayItems: List<DailyForecastEntity.DayItem> = response.items.map { dayItem ->
            DailyForecastEntity.DayItem(
                dateTime = DateTimeValueType(dayItem.date),
                minTemperature = TemperatureValueType(dayItem.minTemp, DEFAULT_TEMPERATURE_UNIT),
                maxTemperature = TemperatureValueType(dayItem.maxTemp, DEFAULT_TEMPERATURE_UNIT),
                items = listOfNotNull(dayItem.amValues, dayItem.pmValues, dayItem.singleValues).map { item ->
                    DailyForecastEntity.DayItem.Item(
                        weatherCondition = WeatherConditionValueType(mapWeatherCondition(item.weatherDescription)),
                        precipitationProbability = ProbabilityValueType(item.pop.toShort(), PercentageUnit),
                    )
                },
            )
        }

        return DailyForecastEntity(dayItems)
    }

    private fun mapYesterdayWeather(response: KmaYesterdayWeatherResponse): YesterdayWeatherEntity {
        return YesterdayWeatherEntity(
            TemperatureValueType(response.temperature, DEFAULT_TEMPERATURE_UNIT),
        )
    }

    private fun mapWeatherCondition(text: String): WeatherConditionCategory {
        return weatherConditionMap.getValue(text)
    }

    override fun map(
        apiResponseModel: ApiResponseModel,
        majorWeatherEntityType: MajorWeatherEntityType,
    ) = when (majorWeatherEntityType) {
        MajorWeatherEntityType.CURRENT_CONDITION -> mapCurrentWeather(apiResponseModel as KmaCurrentWeatherResponse)
        MajorWeatherEntityType.HOURLY_FORECAST -> mapHourlyForecast(apiResponseModel as KmaHourlyForecastResponse)
        MajorWeatherEntityType.DAILY_FORECAST -> mapDailyForecast(apiResponseModel as KmaDailyForecastResponse)
        MajorWeatherEntityType.YESTERDAY_WEATHER -> mapYesterdayWeather(apiResponseModel as KmaYesterdayWeatherResponse)
        else -> throw IllegalArgumentException("Unsupported majorWeatherEntityType: $majorWeatherEntityType")
    }
}