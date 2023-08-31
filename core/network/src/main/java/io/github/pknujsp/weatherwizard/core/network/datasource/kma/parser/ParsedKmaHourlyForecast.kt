package io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser


 data class ParsedKmaHourlyForecast(
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