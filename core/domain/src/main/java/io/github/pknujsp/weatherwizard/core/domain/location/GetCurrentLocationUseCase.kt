package io.github.pknujsp.weatherwizard.core.domain.location

import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val appLocationManager: AppLocationManager
) {
    suspend operator fun invoke(): CurrentLocationResultState = if (appLocationManager.isGpsProviderEnabled) {
        when (val currentLocation = appLocationManager.getCurrentLocation()) {
            is AppLocationManager.LocationResult.Success -> {
                CurrentLocationResultState.Success(currentLocation.location.latitude, currentLocation.location.longitude)
            }

            is AppLocationManager.LocationResult.Failure -> {
                CurrentLocationResultState.Failure(FailedReason.UNKNOWN)
            }
        }
    } else {
        CurrentLocationResultState.Failure(FailedReason.LOCATION_PROVIDER_DISABLED)
    }

}

sealed interface CurrentLocationResultState {
    data class Success(val latitude: Double, val longitude: Double) : CurrentLocationResultState
    data class Failure(val reason: FailedReason) : CurrentLocationResultState
}