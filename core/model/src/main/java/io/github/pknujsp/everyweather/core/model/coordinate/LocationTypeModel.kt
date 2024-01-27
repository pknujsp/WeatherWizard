package io.github.pknujsp.everyweather.core.model.coordinate

data class LocationTypeModel(
    val locationType: LocationType = LocationType.CurrentLocation,
    val address: String = "",
    val country: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)