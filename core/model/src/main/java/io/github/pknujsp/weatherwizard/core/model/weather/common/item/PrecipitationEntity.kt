package io.github.pknujsp.weatherwizard.core.model.weather.common.item

import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationProbabilityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationVolumeType

data class PrecipitationEntity(
    val rainVolume: PrecipitationVolumeType = PrecipitationVolumeType(0.0),
    val snowVolume: PrecipitationVolumeType = PrecipitationVolumeType(0.0),
    val totalVolume: PrecipitationVolumeType = PrecipitationVolumeType(0.0),
    val probability: PrecipitationProbabilityType = PrecipitationProbabilityType(0.0),
) {
    val raining: Boolean
        get() = rainVolume > 0.0

    val snowing: Boolean
        get() = snowVolume > 0.0

    val rainingOrSnowing: Boolean
        get() = raining || snowing || !totalVolume.isEmpty()
}