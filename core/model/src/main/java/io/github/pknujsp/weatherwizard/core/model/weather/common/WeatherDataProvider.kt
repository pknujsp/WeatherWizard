package io.github.pknujsp.weatherwizard.core.model.weather.common

sealed interface WeatherDataProvider {
    data object Kma : WeatherDataProvider
    data object MetNorway : WeatherDataProvider
}