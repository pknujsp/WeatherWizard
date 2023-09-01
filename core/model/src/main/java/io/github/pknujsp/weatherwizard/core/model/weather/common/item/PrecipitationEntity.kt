package io.github.pknujsp.weatherwizard.core.model.weather.common.item

import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationCategoryType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationVolumeType

data class PrecipitationEntity(
    val rainVolume: PrecipitationVolumeType,
    val snowVolume: PrecipitationVolumeType,
    val totalVolume: PrecipitationVolumeType,
    val type: PrecipitationCategoryType,
) {
    val raining: Boolean
        get() = rainVolume > 0.0

    val snowing: Boolean
        get() = snowVolume > 0.0

    val rainingOrSnowing: Boolean
        get() = raining || snowing || !totalVolume.isEmpty()
}