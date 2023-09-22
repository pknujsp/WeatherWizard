package io.github.pknujsp.weatherwizard.core.model.nominatim

import io.github.pknujsp.weatherwizard.core.model.UiModel

data class GeoCode(
    val placeId: Long,
    val displayName: String,
    val countryCode: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val isAdded: Boolean
) : UiModel {
    var onSelected: (() -> Unit)? = null
}