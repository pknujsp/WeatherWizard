package io.github.pknujsp.everyweather.core.network.api.kma.parser


data class ParsedKmaHourlyForecast(
    val dateTime: String,
    val weatherCondition: String,
    val temp: Short,
    val feelsLikeTemp: Short,
    val rainVolume: Double,
    val snowVolume: Double,
    val pop: Int,
    val windDirection: Int,
    val windSpeed: Double,
    val humidity: Int,
    val isHasRain: Boolean = false,
    val isHasSnow: Boolean = false,
    val isHasThunder: Boolean = false,
    val isHasShower: Boolean = false,
)