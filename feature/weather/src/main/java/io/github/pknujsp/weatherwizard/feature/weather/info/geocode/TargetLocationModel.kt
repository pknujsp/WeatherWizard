package io.github.pknujsp.weatherwizard.feature.weather.info.geocode

data class TargetLocationModel(
    val address: String? = null,
    val country: String? = null,
    val latitude: Double,
    val longitude: Double,
)