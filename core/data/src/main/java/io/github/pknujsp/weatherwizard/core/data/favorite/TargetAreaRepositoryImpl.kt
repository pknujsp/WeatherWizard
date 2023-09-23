package io.github.pknujsp.weatherwizard.core.data.favorite

import io.github.pknujsp.weatherwizard.core.database.AppDataStore
import io.github.pknujsp.weatherwizard.core.model.DBEntityState
import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType
import javax.inject.Inject

class TargetAreaRepositoryImpl @Inject constructor(
    private val appDataStore: AppDataStore
) : TargetAreaRepository {

    private val targetAreaKey = "TargetAreaId"

    override suspend fun getTargetArea(): TargetAreaType {
        return appDataStore.readAsLong(targetAreaKey).run {
            if (this is DBEntityState.Exists) {
                if (data == TargetAreaType.CurrentLocation.id) {
                    TargetAreaType.CurrentLocation
                } else {
                    TargetAreaType.CustomLocation(data)
                }
            } else {
                TargetAreaType.CurrentLocation
            }
        }
    }

    override suspend fun updateTargetArea(target: TargetAreaType) {
        appDataStore.save(targetAreaKey, target.id)
    }

}