package io.github.pknujsp.weatherwizard.core.model.coordinate

data class LocationModel(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val country: String,
    val countryCode: String,
)