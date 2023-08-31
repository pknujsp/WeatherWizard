package io.github.pknujsp.weatherwizard.core.data.weather.kma

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherResponseMapper
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaHourlyForecastResponse
import javax.inject.Inject


class KmaResponseMapper @Inject constructor() : WeatherResponseMapper<KmaCurrentWeatherResponse,
        KmaHourlyForecastResponse, KmaDailyForecastResponse> {
    override fun mapCurrentWeather(response: KmaCurrentWeatherResponse): CurrentWeatherEntity {
        return CurrentWeatherEntity(weatherCondition = response.weatherCondition,
            weatherIcon = 0,
            temperature = 0.0,
            feelsLikeTemperature = 0.0,
            humidity = 0.0,
            windSpeed = 0.0,
            windDirection = 0.0,
        )
    }

    override fun mapHourlyForecast(response: KmaHourlyForecastResponse): HourlyForecastEntity {
        TODO("Not yet implemented")
    }

    override fun mapDailyForecast(response: KmaDailyForecastResponse): DailyForecastEntity {
        TODO("Not yet implemented")
    }


}