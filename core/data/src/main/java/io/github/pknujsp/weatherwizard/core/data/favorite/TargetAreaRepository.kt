package io.github.pknujsp.weatherwizard.core.data.favorite

import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType

interface TargetAreaRepository {

    suspend fun updateTargetArea(target: LocationType)

    suspend fun getTargetArea(): LocationType
}