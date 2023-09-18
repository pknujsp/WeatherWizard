package io.github.pknujsp.weatherwizard.core.model.favorite

import io.github.pknujsp.weatherwizard.core.model.UiModel


data class FavoriteAreaList(
    val id: Long,
    val areaName: String,
    val countryName: String,
) : UiModel