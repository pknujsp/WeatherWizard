package io.github.pknujsp.weatherwizard.core.data.favorite

import kotlinx.coroutines.flow.Flow

interface TargetLocationRepository {

    suspend fun updateTargetLocation(newModel: SelectedLocationModel)

    suspend fun getTargetLocation(): SelectedLocationModel

    fun observeTargetLocation(): Flow<SelectedLocationModel>
}