package io.github.pknujsp.everyweather.core.model.weather

import io.github.pknujsp.everyweather.core.model.coordinate.LocationType

data class TargetLocationModel(
    val latitude: Double,
    val longitude: Double,
    val locationType: LocationType,
    val customLocationId: Long? = null,
)
