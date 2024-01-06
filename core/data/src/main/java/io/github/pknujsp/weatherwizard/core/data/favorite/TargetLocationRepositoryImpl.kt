package io.github.pknujsp.weatherwizard.core.data.favorite

import io.github.pknujsp.weatherwizard.core.database.AppDataStore
import io.github.pknujsp.weatherwizard.core.model.DBEntityState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

class TargetLocationRepositoryImpl(
    private val appDataStore: AppDataStore
) : TargetLocationRepository {

    private companion object {
        private const val targetAreaKey = "targetAreaKey"
        private const val customLocationIdKey = "customLocationIdKey"
    }

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

    override fun observeTargetLocation(): Flow<SelectedLocationModel> = appDataStore.observeInt(targetAreaKey).filterNotNull().map {
        LocationType.fromKey(it)
    }.zip(appDataStore.observeLong(customLocationIdKey).filterNotNull()) { locationType, locationId ->
        SelectedLocationModel(locationType, locationId)
    }

    override suspend fun updateTargetLocation(newModel: SelectedLocationModel) {
        appDataStore.save(targetAreaKey, newModel.locationType.key)
        appDataStore.save(customLocationIdKey, newModel.locationId)
    }

}