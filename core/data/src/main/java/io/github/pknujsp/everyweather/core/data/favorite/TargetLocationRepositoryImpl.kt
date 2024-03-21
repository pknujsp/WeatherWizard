package io.github.pknujsp.everyweather.core.data.favorite

import io.github.pknujsp.everyweather.core.data.RepositoryInitializer
import io.github.pknujsp.everyweather.core.database.AppDataStore
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TargetLocationRepositoryImpl(
    private val appDataStore: AppDataStore,
) : TargetLocationRepository, RepositoryInitializer {
    private companion object {
        private const val TARGET_LOCATION_KEY = "TARGET_LOCATION_KEY"
    }

    override val targetLocation
        get() =
            appDataStore.observeString(TARGET_LOCATION_KEY).map { key ->
                key?.run {
                    val (locationType, locationId) = split(",")
                    SelectedLocationModel(LocationType.fromKey(locationType.toInt()), locationId.toLong())
                } ?: run {
                    val defaultLocation = createDefaultLocation()
                    updateTargetLocation(defaultLocation)
                    defaultLocation
                }
            }

    override suspend fun getCurrentTargetLocation() = targetLocation.first()

    override suspend fun updateTargetLocation(newModel: SelectedLocationModel) {
        val value = "${newModel.locationType.key},${if (newModel.locationType is LocationType.CustomLocation) newModel.locationId else 0L}"
        appDataStore.save(TARGET_LOCATION_KEY, value)
    }

    private fun createDefaultLocation() = SelectedLocationModel(LocationType.CurrentLocation, 0L)

    override suspend fun initialize() {
        targetLocation.first()
    }
}
