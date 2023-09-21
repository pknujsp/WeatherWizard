package io.github.pknujsp.weatherwizard.core.model.favorite

import io.github.pknujsp.weatherwizard.core.model.EntityModel

data class FavoriteAreaListEntity(
    val id: Long = 0,
    val areaName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double
) : EntityModel