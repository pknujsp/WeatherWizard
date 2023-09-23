package io.github.pknujsp.weatherwizard.core.model.favorite

import io.github.pknujsp.weatherwizard.core.model.UiModel


data class FavoriteArea(
    val id: Long,
    val placeId: Long,
    val areaName: String,
    val countryName: String,
) : UiModel