package io.github.pknujsp.everyweather.feature.weather.info.geocode

data class TargetLocationModel(
    val address: String? = null,
    val country: String? = null,
    val latitude: Double,
    val longitude: Double,
)