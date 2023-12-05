package io.github.pknujsp.weatherwizard.core.data.favorite

interface TargetLocationRepository {

    suspend fun updateTargetLocation(newModel: SelectedLocationModel)

    suspend fun getTargetLocation(): SelectedLocationModel
}