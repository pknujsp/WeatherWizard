package io.github.pknujsp.weatherwizard.core.domain.location

import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionState
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GetCurrentLocationUseCase @Inject constructor(
    override val appLocationManager: AppLocationManager,
    private val nominatimRepository: NominatimRepository,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher,
) : GetCurrentLocationAddress, GetCurrentLocationCoordinate {

    private val mutableGeoCodeFlow: MutableStateFlow<LocationGeoCodeState?> = MutableStateFlow(null)
    override val geoCodeFlow = mutableGeoCodeFlow.asStateFlow()

    private val mutableCurrentLocationFlow: MutableStateFlow<CurrentLocationState?> = MutableStateFlow(null)
    override val currentLocationFlow = mutableCurrentLocationFlow.asStateFlow()

    override suspend fun invoke(): LocationGeoCodeState {
        return when (val currentLocation = getCurrentLocation()) {
            is CurrentLocationState.Success -> {
                val geoCode = reverseGeoCode(currentLocation.latitude, currentLocation.longitude).await()
                val geoCodeState = if (geoCode is LocationGeoCodeState.Success) {
                    LocationGeoCodeState.Success(currentLocation.latitude, currentLocation.longitude, geoCode.address, geoCode.country)
                } else {
                    LocationGeoCodeState.Failure(FailedReason.REVERSE_GEOCODE_ERROR)
                }
                mutableGeoCodeFlow.emit(geoCodeState)
                geoCodeState
            }

            else -> {
                val geoCodeState = LocationGeoCodeState.Failure((currentLocation as CurrentLocationState.Failure).reason)
                mutableGeoCodeFlow.emit(geoCodeState)
                geoCodeState
            }
        }
    }

    override suspend operator fun invoke(loadAddress: Boolean): CurrentLocationState {
        val currentLocation = getCurrentLocation()
        if (currentLocation is CurrentLocationState.Success && loadAddress) {
            supervisorScope {
                launch {
                    reverseGeoCode(currentLocation.latitude, currentLocation.longitude)
                }
            }
        }
        return currentLocation
    }

    private suspend fun getCurrentLocation(): CurrentLocationState {
        mutableCurrentLocationFlow.emit(CurrentLocationState.Loading())
        if (!appLocationManager.isPermissionGranted) {
            val state = CurrentLocationState.Failure(PermissionType.LOCATION)
            mutableCurrentLocationFlow.emit(state)
            return state
        }

        val result = if (appLocationManager.isGpsProviderEnabled) {
            when (val currentLocation = appLocationManager.getCurrentLocation()) {
                is AppLocationManager.LocationResult.Success -> {
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
        return result
    }

    private suspend fun reverseGeoCode(latitude: Double, longitude: Double): Deferred<LocationGeoCodeState> {
        val newDeferred = supervisorScope {
            async(dispatcher) {
                val result = nominatimRepository.reverseGeoCode(latitude, longitude).fold(onSuccess = { address ->
                    LocationGeoCodeState.Success(latitude, longitude, address.simpleDisplayName, address.country)
                }, onFailure = {
                    LocationGeoCodeState.Failure(FailedReason.REVERSE_GEOCODE_ERROR)
                })
                result
            }
        }
        return newDeferred
    }
}


interface CurrentLocationUseCase {
    val appLocationManager: AppLocationManager
}