package io.github.pknujsp.everyweather.core.model.favorite

import io.github.pknujsp.everyweather.core.model.EntityModel

data class FavoriteAreaListEntity(
    val id: Long = 0,
    val placeId: Long,
    val areaName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double,
) : EntityModel
