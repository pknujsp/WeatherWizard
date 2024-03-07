package io.github.pknujsp.everyweather.core.network.api.kma

import io.github.pknujsp.everyweather.core.network.api.CurrentWeatherResponseModel
import io.github.pknujsp.everyweather.core.network.api.DailyForecastResponseModel
import io.github.pknujsp.everyweather.core.network.api.HourlyForecastResponseModel
import io.github.pknujsp.everyweather.core.network.api.YesterdayWeatherResponseModel
import io.github.pknujsp.everyweather.core.network.api.kma.parser.ParsedKmaCurrentWeather
import io.github.pknujsp.everyweather.core.network.api.kma.parser.ParsedKmaDailyForecast
import io.github.pknujsp.everyweather.core.network.api.kma.parser.ParsedKmaHourlyForecast


class KmaCurrentWeatherResponse(
    currentWeather: ParsedKmaCurrentWeather,
    hourlyForecast: ParsedKmaHourlyForecast,
) : CurrentWeatherResponseModel {

    val weatherCondition: String = hourlyForecast.weatherCondition
    val temperature: Short = currentWeather.temperature
    val feelsLikeTemperature: Short = currentWeather.feelsLikeTemperature
    val humidity: Int = currentWeather.humidity
    val windSpeed: Double = currentWeather.windSpeed
    val windDirection: Int = currentWeather.windDirection
    val precipitationVolume: Double = currentWeather.precipitationVolume
    val precipitationType: String = currentWeather.precipitationType
}

class KmaHourlyForecastResponse(
    hourlyForecasts: List<ParsedKmaHourlyForecast>,
) : HourlyForecastResponseModel {

    val items: List<Item> = hourlyForecasts.map {
        Item(
            dateTime = it.dateTime,
            isHasShower = it.isHasShower,
            weatherDescription = it.weatherCondition,
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
        val dateTime: String,
        val isHasShower: Boolean = false,
        val weatherDescription: String,
        val temp: Double,
        val feelsLikeTemp: Double,
        val rainVolume: Double,
        val snowVolume: Double,
        val pop: Int,
        val windDirection: Int,
        val windSpeed: Double,
        val humidity: Int,
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
            date = it.date,
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
        val date: String,
        val isSingle: Boolean = false,
        val amValues: Values? = null,
        val pmValues: Values? = null,
        val singleValues: Values? = null,
        val minTemp: Double,
        val maxTemp: Double,
    ) {
        data class Values(
            var weatherDescription: String,
            var pop: Int,
        )

    }
}

class KmaYesterdayWeatherResponse(
    currentWeather: ParsedKmaCurrentWeather,
) : YesterdayWeatherResponseModel {
    val temperature: Double = currentWeather.yesterdayTemperature
}