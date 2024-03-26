package io.github.pknujsp.everyweather.core.data.favorite

import io.github.pknujsp.everyweather.core.model.coordinate.LocationType

data class SelectedLocationModel(
    val locationType: LocationType,
    val locationId: Long = -1L,
)