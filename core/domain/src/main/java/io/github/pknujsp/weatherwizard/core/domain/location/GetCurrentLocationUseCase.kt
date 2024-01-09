package io.github.pknujsp.weatherwizard.core.domain.location

import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.common.util.toCoordinate
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val appLocationManager: AppLocationManager,
    private val nominatimRepository: NominatimRepository,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
) {

    private val mutableCurrentLocationFlow =
        MutableSharedFlow<CurrentLocationResultState>(1, 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val currentLocationFlow = mutableCurrentLocationFlow.asSharedFlow()

    suspend operator fun invoke(): CurrentLocationResultState {
        if (!appLocationManager.isPermissionGranted) {
            val state = CurrentLocationResultState.Failure(FailedReason.LOCATION_PERMISSION_DENIED)
            mutableCurrentLocationFlow.emit(state)
            return state
        }

        val result = if (appLocationManager.isGpsProviderEnabled) {
            when (val currentLocation = appLocationManager.getCurrentLocation()) {
                is AppLocationManager.LocationResult.Success -> CurrentLocationResultState.Success(currentLocation.location.latitude.toCoordinate(),
                    currentLocation.location.longitude.toCoordinate())

                is AppLocationManager.LocationResult.Failure -> CurrentLocationResultState.Failure(FailedReason.UNKNOWN)
            }
        } else {
            CurrentLocationResultState.Failure(FailedReason.LOCATION_PROVIDER_DISABLED)
        }
        mutableCurrentLocationFlow.emit(result)
        return result
    }

    suspend fun getCurrentLocationWithAddress(): CurrentLocationResultState {
        val currentLocation = invoke()
        val result = if (currentLocation is CurrentLocationResultState.Success) {
            withContext(ioDispatcher) {
                nominatimRepository.reverseGeoCode(currentLocation.latitude, currentLocation.longitude).fold(onSuccess = { address ->
                    CurrentLocationResultState.SuccessWithAddress(currentLocation.latitude,
                        currentLocation.longitude,
                        address.simpleDisplayName,
                        address.country)
                }, onFailure = {
                    CurrentLocationResultState.Failure(FailedReason.REVERSE_GEOCODE_ERROR)
                })
            }
        } else {
            currentLocation
        }
        mutableCurrentLocationFlow.emit(result)
        return result
    }

    suspend fun updateCurrentLocationAddress(latitude: Double, longitude: Double, address: String, country: String) {
        mutableCurrentLocationFlow.emit(CurrentLocationResultState.SuccessWithAddress(latitude, longitude, address, country))
    }
}

sealed interface CurrentLocationResultState {
    data class Success(val latitude: Double, val longitude: Double) : CurrentLocationResultState
    data class Failure(val reason: FailedReason) : CurrentLocationResultState
    data class SuccessWithAddress(val latitude: Double, val longitude: Double, val address: String, val country: String) :
        CurrentLocationResultState
}