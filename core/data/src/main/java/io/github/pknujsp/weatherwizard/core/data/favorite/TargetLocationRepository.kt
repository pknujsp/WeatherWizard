package io.github.pknujsp.weatherwizard.core.data.favorite

import kotlinx.coroutines.flow.Flow

interface TargetLocationRepository {

    val targetLocation: Flow<SelectedLocationModel>

    suspend fun getCurrentTargetLocation(): SelectedLocationModel

    suspend fun updateTargetLocation(newModel: SelectedLocationModel)
}