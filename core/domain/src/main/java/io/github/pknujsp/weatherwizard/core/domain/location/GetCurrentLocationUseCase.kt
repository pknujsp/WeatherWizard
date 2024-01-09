package io.github.pknujsp.weatherwizard.core.domain.location

import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.common.util.toCoordinate
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentLocationUseCase @Inject constructor(
    private val appLocationManager: AppLocationManager,
    private val nominatimRepository: NominatimRepository,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
) {
    private var reverseGeoCodeJob: Job? = null

    private val mutableCurrentLocationFlow =
        MutableSharedFlow<CurrentLocationResultState>(3, 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val currentLocationFlow = mutableCurrentLocationFlow.asSharedFlow()

    suspend operator fun invoke(asyncLoadAddress: Boolean = false, time: Long = System.currentTimeMillis()): CurrentLocationResultState {
        if (!appLocationManager.isPermissionGranted) {
            val state = CurrentLocationResultState.Failure(FailedReason.LOCATION_PERMISSION_DENIED, time)
            mutableCurrentLocationFlow.emit(state)
            return state
        }

        val result = if (appLocationManager.isGpsProviderEnabled) {
            when (val currentLocation = appLocationManager.getCurrentLocation()) {
                is AppLocationManager.LocationResult.Success -> {
                    if (asyncLoadAddress) {
                        reverseGeoCodeJob?.cancel()
                        reverseGeoCodeJob = supervisorScope {
                            launch(ioDispatcher) {
                                mutableCurrentLocationFlow.emit(reverseGeoCode(currentLocation.location.latitude.toCoordinate(),
                                    currentLocation.location.longitude.toCoordinate(),
                                    System.currentTimeMillis()))
                            }
                        }
                    }

                    CurrentLocationResultState.Success(currentLocation.location.latitude.toCoordinate(),
                        currentLocation.location.longitude.toCoordinate(),
                        time)
                }

                is AppLocationManager.LocationResult.Failure -> CurrentLocationResultState.Failure(FailedReason.UNKNOWN, time)
            }
        } else {
            CurrentLocationResultState.Failure(FailedReason.LOCATION_PROVIDER_DISABLED, time)
        }
        mutableCurrentLocationFlow.emit(result)
        return result
    }

    suspend fun getCurrentLocationWithAddress(time: Long = System.currentTimeMillis()): CurrentLocationResultState {
        val currentLocation = invoke(false, time)
        val result = if (currentLocation is CurrentLocationResultState.Success) {
            withContext(ioDispatcher) {
                reverseGeoCode(currentLocation.latitude, currentLocation.longitude, time)
            }
        } else {
            currentLocation
        }
        mutableCurrentLocationFlow.emit(result)
        return result
    }

    private suspend fun reverseGeoCode(latitude: Double, longitude: Double, time: Long) =
        nominatimRepository.reverseGeoCode(latitude, longitude).fold(onSuccess = { address ->
            CurrentLocationResultState.SuccessWithAddress(latitude, longitude, address.simpleDisplayName, address.country, time)
        }, onFailure = {
            CurrentLocationResultState.Failure(FailedReason.REVERSE_GEOCODE_ERROR, time)
        })

}

sealed interface CurrentLocationResultState {
    val time: Long

    data class Success(val latitude: Double, val longitude: Double, override val time: Long) : CurrentLocationResultState
    data class Failure(val reason: FailedReason, override val time: Long) : CurrentLocationResultState
    data class SuccessWithAddress(
        val latitude: Double, val longitude: Double, val address: String, val country: String, override val time: Long
    ) : CurrentLocationResultState
}