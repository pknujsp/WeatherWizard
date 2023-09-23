package io.github.pknujsp.weatherwizard.core.data.favorite

import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType

interface TargetAreaRepository {

    suspend fun updateTargetArea(target: TargetAreaType)

    suspend fun getTargetArea(): TargetAreaType
}