package io.github.pknujsp.weatherwizard.core.domain.location

import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationModel
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val appLocationManager: AppLocationManager,
    private val nominatimRepository: NominatimRepository
) {
    suspend operator fun invoke(): CurrentLocationResultState {
        if (appLocationManager.isGpsProviderEnabled) {
            val currentLocation = appLocationManager.getCurrentLocation()
            val (latitude, longitude) = when (currentLocation) {
                is AppLocationManager.LocationResult.Success -> {
                    currentLocation.location.latitude to currentLocation.location.longitude
                }

                is AppLocationManager.LocationResult.Failure -> {
                    return CurrentLocationResultState.Failure(CurrentLocationResultState.FailureReason.UNKNOWN)
                }
            }

            val reverseGeoCode = nominatimRepository.reverseGeoCode(latitude, longitude)
            val result = reverseGeoCode.fold(
                onSuccess = {
                    CurrentLocationResultState.Success(LocationModel(latitude, longitude, it.simpleDisplayName))
                },
                onFailure = {
                    CurrentLocationResultState.Failure(CurrentLocationResultState.FailureReason.UNKNOWN)
                }
            )

            return result
        } else {
            return CurrentLocationResultState.Failure(CurrentLocationResultState.FailureReason.GPS_PROVIDER_DISABLED)
        }
    }
}

sealed interface CurrentLocationResultState {
    data class Success(val location: LocationModel) : CurrentLocationResultState
    data class Failure(val reason: FailureReason) : CurrentLocationResultState

    enum class FailureReason {
        GPS_PROVIDER_DISABLED,
        UNKNOWN
    }
}