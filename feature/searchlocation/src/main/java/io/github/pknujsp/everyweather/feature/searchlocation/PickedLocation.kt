package io.github.pknujsp.everyweather.feature.searchlocation

import io.github.pknujsp.everyweather.core.model.UiModel

data class PickedLocation(
    val id: Long = 0,
    val placeId: Long,
    val addressName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double
) : UiModel