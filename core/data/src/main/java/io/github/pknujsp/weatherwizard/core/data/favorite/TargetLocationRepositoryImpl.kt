package io.github.pknujsp.weatherwizard.core.data.favorite

import io.github.pknujsp.weatherwizard.core.database.AppDataStore
import io.github.pknujsp.weatherwizard.core.model.DBEntityState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import javax.inject.Inject

class TargetLocationRepositoryImpl(
    private val appDataStore: AppDataStore
) : TargetLocationRepository {

    private val targetAreaKey = "targetAreaKey"
    private val customLocationIdKey = "customLocationIdKey"

    override suspend fun getTargetLocation(): SelectedLocationModel {
        val selectedLocationType = appDataStore.readAsInt(targetAreaKey).run {
            if (this is DBEntityState.Exists) {
                LocationType.fromKey(data)
            } else {
                LocationType.CurrentLocation
            }
        }
        val selectedLocationId = appDataStore.readAsLong(customLocationIdKey).run {
            if (this is DBEntityState.Exists) {
                data
            } else {
                0
            }
        }

        return SelectedLocationModel(selectedLocationType, selectedLocationId)
    }

    override suspend fun updateTargetLocation(newModel: SelectedLocationModel) {
        appDataStore.save(targetAreaKey, newModel.locationType.key)
        appDataStore.save(customLocationIdKey, newModel.locationId)
    }

}