package io.github.pknujsp.weatherwizard.core.network.datasource.kma

import io.github.pknujsp.weatherwizard.core.network.datasource.CurrentWeatherResponseModel
import io.github.pknujsp.weatherwizard.core.network.datasource.DailyForecastResponseModel
import io.github.pknujsp.weatherwizard.core.network.datasource.HourlyForecastResponseModel
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser.ParsedKmaCurrentWeather
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser.ParsedKmaDailyForecast
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser.ParsedKmaHourlyForecast


class KmaCurrentWeatherResponse(
    currentWeather: ParsedKmaCurrentWeather,
    hourlyForecast: KmaHourlyForecastResponse.Item,
) : CurrentWeatherResponseModel {

    val weatherCondition: String = hourlyForecast.weatherDescription
    val temperature: Double = currentWeather.temp.toDouble()
    val feelsLikeTemperature: Double = currentWeather.feelsLikeTemp.toDouble()
    val humidity: Double = currentWeather.humidity.toDouble()
    val windSpeed: Double = currentWeather.windSpeed.toDouble()
    val windDirection: Double = currentWeather.windDirection.toDouble()
    val yesterdayTemperature: Double = currentWeather.yesterdayTemp.toDouble()
    val precipitationVolume: Double = currentWeather.precipitationVolume.toDouble()
    val pty: String = currentWeather.pty
}

class KmaHourlyForecastResponse(
    hourlyForecasts: List<ParsedKmaHourlyForecast>,
) : HourlyForecastResponseModel {

    val items: List<Item> = hourlyForecasts.map {
        Item(
            hourISO8601 = it.hourISO8601,
            isHasShower = it.isHasShower,
            weatherDescription = it.weatherDescription,
            temp = it.temp,
            feelsLikeTemp = it.feelsLikeTemp,
            rainVolume = it.rainVolume,
            snowVolume = it.snowVolume,
            pop = it.pop,
            windDirection = it.windDirection,
            windSpeed = it.windSpeed,
            humidity = it.humidity,
            isHasRain = it.isHasRain,
            isHasSnow = it.isHasSnow,
            isHasThunder = it.isHasThunder,
        )
    }

    data class Item(
        val hourISO8601: String = "",
        val isHasShower: Boolean = false,
        val weatherDescription: String = "",
        val temp: String = "",
        val feelsLikeTemp: String = "",
        val rainVolume: String = "",
        val snowVolume: String = "",
        val pop: String = "",
        val windDirection: String = "",
        val windSpeed: String = "",
        val humidity: String = "",
        val isHasRain: Boolean = false,
        val isHasSnow: Boolean = false,
        val isHasThunder: Boolean = false,
    )
}

class KmaDailyForecastResponse(
    dailyForecasts: List<ParsedKmaDailyForecast>,
) : DailyForecastResponseModel {
    val items: List<Item> = dailyForecasts.map {
        Item(
            dateISO8601 = it.dateISO8601,
            isSingle = it.isSingle,
            amValues = it.amValues?.let { amValues ->
                Item.Values(
                    weatherDescription = amValues.weatherDescription,
                    pop = amValues.pop,
                )
            },
            pmValues = it.pmValues?.let { pmValues ->
                Item.Values(
                    weatherDescription = pmValues.weatherDescription,
                    pop = pmValues.pop,
                )
            },
            singleValues = it.singleValues?.let { singleValues ->
                Item.Values(
                    weatherDescription = singleValues.weatherDescription,
                    pop = singleValues.pop,
                )
            },
            minTemp = it.minTemp,
            maxTemp = it.maxTemp,
        )
    }

    data class Item(
        val dateISO8601: String = "",
        val isSingle: Boolean = false,
        val amValues: Values? = null,
        val pmValues: Values? = null,
        val singleValues: Values? = null,
        val minTemp: String = "",
        val maxTemp: String = "",
    ) {
        data class Values(
            var weatherDescription: String = "",
            var pop: String = "",
        )

    }
}