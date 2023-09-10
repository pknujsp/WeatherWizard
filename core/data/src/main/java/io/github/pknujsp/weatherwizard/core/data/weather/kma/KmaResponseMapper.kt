package io.github.pknujsp.weatherwizard.core.data.weather.kma

import io.github.pknujsp.weatherwizard.core.common.util.toLeastZero
import io.github.pknujsp.weatherwizard.core.data.weather.DefaultValueUnit
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapper
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PercentageUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PressureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.RainfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.VisibilityUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaHourlyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaYesterdayWeatherResponse
import javax.inject.Inject


class KmaResponseMapper @Inject constructor() :
    WeatherResponseMapper<KmaCurrentWeatherResponse, KmaHourlyForecastResponse, KmaDailyForecastResponse, KmaYesterdayWeatherResponse> {

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

    override fun mapCurrentWeather(response: KmaCurrentWeatherResponse): CurrentWeatherEntity {
        return response.run {
            CurrentWeatherEntity(
                weatherCondition = WeatherConditionValueType(mapWeatherCondition(weatherCondition)),
                temperature = TemperatureValueType(temperature, DEFAULT_TEMPERATURE_UNIT),
                humidity = HumidityValueType(humidity, PercentageUnit),
                windSpeed = WindSpeedValueType(windSpeed, DEFAULT_WIND_SPEED_UNIT),
                windDirection = WindDirectionValueType(windDirection, DEFAULT_WIND_DIRECTION_UNIT),
                feelsLikeTemperature = TemperatureValueType(feelsLikeTemperature, DEFAULT_TEMPERATURE_UNIT),
                precipitationVolume = PrecipitationValueType(precipitationVolume, DEFAULT_PRECIPITATION_UNIT),
            )
        }
    }

    override fun mapHourlyForecast(response: KmaHourlyForecastResponse): HourlyForecastEntity {
        val list = response.items.map { item ->
            HourlyForecastEntity.Item(
                dateTime = DateTimeValueType(item.dateTime),
                weatherCondition = WeatherConditionValueType(mapWeatherCondition(item.weatherDescription)),
                temperature = TemperatureValueType(item.temp, DEFAULT_TEMPERATURE_UNIT),
                humidity = HumidityValueType(item.humidity, PercentageUnit),
                windSpeed = WindSpeedValueType(item.windSpeed, DEFAULT_WIND_SPEED_UNIT),
                windDirection = WindDirectionValueType(item.windDirection, DEFAULT_WIND_DIRECTION_UNIT),
                feelsLikeTemperature = TemperatureValueType(item.feelsLikeTemp, DEFAULT_TEMPERATURE_UNIT),
                rainfallVolume = RainfallValueType(item.rainVolume, DEFAULT_PRECIPITATION_UNIT),
                snowfallVolume = SnowfallValueType(item.snowVolume, DEFAULT_PRECIPITATION_UNIT),
                precipitationVolume = PrecipitationValueType(if (item.rainVolume.isNaN() and item.snowVolume.isNaN())
                    PrecipitationValueType.none.value else item.rainVolume.toLeastZero() + item.snowVolume.toLeastZero(),
                    DEFAULT_PRECIPITATION_UNIT),
                precipitationProbability = ProbabilityValueType(item.pop, PercentageUnit),
            )
        }

        return HourlyForecastEntity(items = list)
    }

    override fun mapDailyForecast(response: KmaDailyForecastResponse): DailyForecastEntity {
        val items: List<List<DailyForecastEntity.Item>> = response.items.map { item ->
            val dateTime = item.date
            val minTemp = item.minTemp
            val maxTemp = item.maxTemp

            listOfNotNull(item.amValues, item.pmValues, item.singleValues).map { item ->
                DailyForecastEntity.Item(
                    dateTime = DateTimeValueType(dateTime),
                    weatherCondition = WeatherConditionValueType(mapWeatherCondition(item.weatherDescription)),
                    minTemperature = TemperatureValueType(minTemp, DEFAULT_TEMPERATURE_UNIT),
                    maxTemperature = TemperatureValueType(maxTemp, DEFAULT_TEMPERATURE_UNIT),
                    precipitationProbability = ProbabilityValueType(item.pop, PercentageUnit),
                )
            }
        }

        return DailyForecastEntity(items = items.flatten())
    }

    override fun mapYesterdayWeather(response: KmaYesterdayWeatherResponse): YesterdayWeatherEntity {
        return YesterdayWeatherEntity(
            TemperatureValueType(response.temperature, DEFAULT_TEMPERATURE_UNIT),
        )
    }


    private fun mapWeatherCondition(text: String): WeatherConditionCategory {
        return weatherConditionMap.getValue(text)
    }

}