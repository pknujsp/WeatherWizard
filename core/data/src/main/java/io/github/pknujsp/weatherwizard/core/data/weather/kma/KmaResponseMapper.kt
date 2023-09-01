package io.github.pknujsp.weatherwizard.core.data.weather.kma

import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapper
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationProbabilityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDoubleValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherTextValueType
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
import javax.inject.Inject


class KmaResponseMapper @Inject constructor() :
    WeatherResponseMapper<KmaCurrentWeatherResponse, KmaHourlyForecastResponse, KmaDailyForecastResponse, KmaCurrentWeatherResponse> {
    override fun mapCurrentWeather(response: KmaCurrentWeatherResponse): CurrentWeatherEntity {
        return response.run {
            CurrentWeatherEntity(weatherCondition = WeatherTextValueType(weatherCondition),
                temperature = TemperatureEntity(current = TemperatureType(temperature), feelsLike = TemperatureType(feelsLikeTemperature)),
                humidity = WeatherDoubleValueType(humidity),
                windEntity = WindEntity(speed = WeatherDoubleValueType(windSpeed),
                    direction = WeatherDoubleValueType(windDirection),
                    directionDegree = WeatherDoubleValueType(windDirection)),
                precipitation = PrecipitationEntity(totalVolume = WeatherDoubleValueType(precipitationVolume)))
        }
    }

    override fun mapHourlyForecast(response: KmaHourlyForecastResponse): HourlyForecastEntity {
        val list = response.items.map { item ->
            HourlyForecastEntity.Item(
                dateTime = WeatherTextValueType(item.hourISO8601),
                weatherCondition = WeatherTextValueType(item.weatherDescription),
                temperature = TemperatureEntity(current = WeatherDoubleValueType(item.temp.toDouble()),
                    feelsLike = WeatherDoubleValueType(item.feelsLikeTemp.toDouble())),
                humidity = WeatherDoubleValueType(item.humidity.toDouble()),
                wind = WindEntity(speed = WeatherDoubleValueType(item.windSpeed.toDouble()),
                    direction = WeatherDoubleValueType(item.windDirection.toDouble()),
                    directionDegree = WeatherDoubleValueType(item.windDirection.toDouble())),
                precipitation = PrecipitationEntity(rainVolume = WeatherDoubleValueType(item.rainVolume.toDouble()),
                    snowVolume = WeatherDoubleValueType(item.snowVolume.toDouble()),
                    totalVolume = WeatherDoubleValueType(item.rainVolume.toDouble() + item.snowVolume.toDouble())),
                thunder = item.isHasThunder,
            )
        }

        return HourlyForecastEntity(items = list)
    }

    override fun mapDailyForecast(response: KmaDailyForecastResponse): DailyForecastEntity {
        val items: List<List<DailyForecastEntity.Item>> = response.items.map { item ->
            val dateTime = item.dateISO8601
            val minTemp = item.minTemp
            val maxTemp = item.maxTemp

            listOfNotNull(item.amValues, item.pmValues, item.singleValues).map { values ->
                DailyForecastEntity.Item(
                    dateTime = DateTimeType(dateTime),
                    weatherCondition = WeatherConditionType(values.weatherDescription),
                    precipitation = PrecipitationEntity(probability = PrecipitationProbabilityType(values.pop.toDouble())),
                    temperature = TemperatureEntity(current = TemperatureType(minTemp.toDouble()),
                        max = TemperatureType(maxTemp.toDouble()),
                        min = TemperatureType(minTemp.toDouble())),
                )
            }
        }

        return DailyForecastEntity(items = items.flatten())
    }

    override fun mapYesterdayWeather(response: KmaCurrentWeatherResponse): YesterdayWeatherEntity {
        return response.run {
            YesterdayWeatherEntity(
                temperature = TemperatureEntity(current = TemperatureType(yesterdayTemperature)),
            )
        }
    }


}