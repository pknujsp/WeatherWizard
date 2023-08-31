package io.github.pknujsp.weatherwizard.core.model.weather

import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity

data class WeatherInfoEntity(
    val currentWeatherEntity: CurrentWeatherEntity,
)