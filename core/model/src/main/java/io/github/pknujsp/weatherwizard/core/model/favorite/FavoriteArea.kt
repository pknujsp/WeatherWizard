package io.github.pknujsp.weatherwizard.core.model.favorite

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.UiModel


@Stable
data class FavoriteArea(
    val id: Long,
    val placeId: Long,
    val areaName: String,
    val countryName: String,
) : UiModel