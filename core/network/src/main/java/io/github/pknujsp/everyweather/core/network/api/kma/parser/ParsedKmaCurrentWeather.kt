package io.github.pknujsp.everyweather.core.network.api.kma.parser


data class ParsedKmaCurrentWeather(
    val dateTime: String,
    val temperature: Double,
    val yesterdayTemperature: Double,
    val feelsLikeTemperature: Double,
    val humidity: Int,
    val windDirection: Int,
    val windSpeed: Double,
    val precipitationVolume: Double,
    val precipitationType: String
)