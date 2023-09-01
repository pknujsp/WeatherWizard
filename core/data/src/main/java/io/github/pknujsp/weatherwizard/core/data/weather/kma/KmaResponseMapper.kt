package io.github.pknujsp.weatherwizard.core.data.weather.kma

import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapper
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationProbabilityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDoubleValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherTextValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedType
import io.github.pknujsp.weatherwizard.core.model.weather.common.item.PrecipitationEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.item.TemperatureEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.item.WindEntity
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
    override fun mapCurrentWeather(response: KmaCurrentWeatherResponse): CurrentWeatherEntity {
        return response.run {
            CurrentWeatherEntity(weatherCondition = WeatherTextValueType(weatherCondition),
                temperature = TemperatureEntity(current = TemperatureType(temperature),
                    feelsLike = TemperatureType(feelsLikeTemperature)),
                humidity = HumidityType(humidity),
                windEntity = WindEntity(speed = WeatherDoubleValueType(windSpeed),
                    direction = WindDirectionType(windDirection)),
                precipitation = PrecipitationEntity(totalVolume = WeatherDoubleValueType(precipitationVolume)))
        }
    }

    override fun mapHourlyForecast(response: KmaHourlyForecastResponse): HourlyForecastEntity {
        val list = response.items.map { item ->
            HourlyForecastEntity.Item(
                dateTime = WeatherTextValueType(item.dateTime),
                weatherCondition = WeatherTextValueType(item.weatherDescription),
                temperature = TemperatureEntity(current = TemperatureType(item.temp),
                    feelsLike = TemperatureType(item.feelsLikeTemp)),
                humidity = HumidityType(item.humidity),
                wind = WindEntity(speed = WindSpeedType(item.windSpeed),
                    direction = WindDirectionType(item.windDirection)),
                precipitation = PrecipitationEntity(rainVolume = WeatherDoubleValueType(item.rainVolume),
                    snowVolume = WeatherDoubleValueType(item.snowVolume),
                    totalVolume = WeatherDoubleValueType(item.rainVolume + item.snowVolume)),
                thunder = item.isHasThunder,
            )
        }

        return HourlyForecastEntity(items = list)
    }

    override fun mapDailyForecast(response: KmaDailyForecastResponse): DailyForecastEntity {
        val items: List<List<DailyForecastEntity.Item>> = response.items.map { item ->
            val dateTime = item.date
            val minTemp = item.minTemp
            val maxTemp = item.maxTemp

            listOfNotNull(item.amValues, item.pmValues, item.singleValues).map { values ->
                DailyForecastEntity.Item(
                    dateTime = DateTimeType(dateTime),
                    weatherCondition = WeatherConditionType(values.weatherDescription),
                    precipitation = PrecipitationEntity(probability = PrecipitationProbabilityType(values.pop)),
                    temperature = TemperatureEntity(current = TemperatureType(minTemp),
                        max = TemperatureType(maxTemp),
                        min = TemperatureType(minTemp)),
                )
            }
        }

        return DailyForecastEntity(items = items.flatten())
    }

    override fun mapYesterdayWeather(response: KmaYesterdayWeatherResponse): YesterdayWeatherEntity {
        return YesterdayWeatherEntity(
            temperature = TemperatureEntity(current = TemperatureType(response.temperature)),
        )
    }

}