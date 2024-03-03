package io.github.pknujsp.everyweather.core.model.weather

import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

data class RequestWeatherArguments(
    val weatherProvider: WeatherProvider,
    val targetLocation: TargetLocationModel,
)