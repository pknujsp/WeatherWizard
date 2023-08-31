package io.github.pknujsp.weatherwizard.core.model

import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather

data class WeatherInfo(
    val currentWeather: CurrentWeather,
)