package io.github.pknujsp.weatherwizard.core.data.favorite

import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType

data class SelectedLocationModel(
    val locationType: LocationType,
    val locationId: Long = 0,
)