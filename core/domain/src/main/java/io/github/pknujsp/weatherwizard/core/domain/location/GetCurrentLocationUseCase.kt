package io.github.pknujsp.weatherwizard.core.domain.location

import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentLocationUseCase @Inject constructor(
    private val appLocationManager: AppLocationManager,
    private val nominatimRepository: NominatimRepository,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher,
) {
    private var reverseGeoCodeJob: Job? = null
    private val mutex = Mutex()

    private val mutableGeoCodeFlow: MutableStateFlow<LocationGeoCodeState?> = MutableStateFlow(null)
    val geoCodeFlow = mutableGeoCodeFlow.asStateFlow()

    private val mutableCurrentLocationFlow: MutableStateFlow<CurrentLocationState?> = MutableStateFlow(null)
    val currentLocationFlow = mutableCurrentLocationFlow.asStateFlow()

    suspend operator fun invoke() {
        if (!appLocationManager.isPermissionGranted) {
            val state = CurrentLocationState.Failure(FailedReason.LOCATION_PERMISSION_DENIED)
            mutableCurrentLocationFlow.emit(state)
            return
        }

        val result = if (appLocationManager.isGpsProviderEnabled) {
            when (val currentLocation = appLocationManager.getCurrentLocation()) {
                is AppLocationManager.LocationResult.Success -> {
                    reverseGeoCode(currentLocation.latitude, currentLocation.longitude)
                    CurrentLocationState.Success(
                        currentLocation.latitude,
                        currentLocation.longitude,
                    )
                }

                is AppLocationManager.LocationResult.Failure -> CurrentLocationState.Failure(FailedReason.UNKNOWN)
            }
        } else {
            CurrentLocationState.Failure(FailedReason.LOCATION_PROVIDER_DISABLED)
        }
        mutableCurrentLocationFlow.emit(result)
    }

    private suspend fun reverseGeoCode(latitude: Double, longitude: Double) {
        mutex.withLock {
            reverseGeoCodeJob?.cancel()
            reverseGeoCodeJob = supervisorScope {
                launch(dispatcher) {
                    val result = nominatimRepository.reverseGeoCode(latitude, longitude).fold(onSuccess = { address ->
                        LocationGeoCodeState.Success(latitude, longitude, address.simpleDisplayName, address.country)
                    }, onFailure = {
                        LocationGeoCodeState.Failure(latitude, longitude, FailedReason.REVERSE_GEOCODE_ERROR)
                    })
                    mutableGeoCodeFlow.emit(result)
                }
            }
        }
    }
}

sealed interface CurrentLocationState {
    val time: LocalDateTime

    data class Failure(val reason: FailedReason, override val time: LocalDateTime = LocalDateTime.now()) : CurrentLocationState
    data class Success(
        val latitude: Double, val longitude: Double, override val time: LocalDateTime = LocalDateTime.now()
    ) : CurrentLocationState
}

sealed interface LocationGeoCodeState {
    val latitude: Double
    val longitude: Double

    data class Success(override val latitude: Double, override val longitude: Double, val address: String, val country: String) :
        LocationGeoCodeState

    data class Failure(override val latitude: Double, override val longitude: Double, val reason: FailedReason) : LocationGeoCodeState
}