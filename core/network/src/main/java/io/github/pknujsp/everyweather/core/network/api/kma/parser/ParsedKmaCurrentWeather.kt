package io.github.pknujsp.everyweather.core.network.api.kma.parser

data class ParsedKmaCurrentWeather(
    val dateTime: String,
    val temperature: Short,
    val yesterdayTemperature: Short,
    val feelsLikeTemperature: Short,
    val humidity: Int,
    val windDirection: Int,
    val windSpeed: Double,
    val precipitationVolume: Double,
    val precipitationType: String,
)
