package io.github.pknujsp.weatherwizard.core.data.favorite

import io.github.pknujsp.weatherwizard.core.database.AppDataStore
import io.github.pknujsp.weatherwizard.core.model.DBEntityState
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import javax.inject.Inject

class TargetAreaRepositoryImpl @Inject constructor(
    private val appDataStore: AppDataStore
) : TargetAreaRepository {

    private val targetAreaKey = "TargetAreaId"

    override suspend fun getTargetArea(): LocationType {
        return appDataStore.readAsLong(targetAreaKey).run {
            if (this is DBEntityState.Exists) {
                if (data == LocationType.CurrentLocation.key.toLong()) {
                    LocationType.CurrentLocation
                } else {
                    LocationType.CustomLocation(data)
                }
            } else {
                LocationType.CurrentLocation
            }
        }
    }

    override suspend fun updateTargetArea(target: LocationType) {
        appDataStore.save(targetAreaKey, if (target is LocationType.CustomLocation) target.locationId else target.key.toLong())
    }

}