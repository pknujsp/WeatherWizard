package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.UiModel

data class CustomLocation(
    val id: Long = 0,
    val placeId: Long,
    val addressName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double
) : UiModel