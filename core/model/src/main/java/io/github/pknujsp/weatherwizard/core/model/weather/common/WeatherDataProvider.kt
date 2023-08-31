package io.github.pknujsp.weatherwizard.core.model.weather.common

sealed interface WeatherDataProvider {
    object Kma : WeatherDataProvider
    object MetNorway : WeatherDataProvider

}