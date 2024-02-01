package io.github.pknujsp.everyweather.core.domain.location

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.everyweather.core.common.manager.AppLocationManager
import io.github.pknujsp.everyweather.core.common.FailedReason
import io.github.pknujsp.everyweather.core.data.nominatim.NominatimRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GetCurrentLocationUseCase @Inject constructor(
    @ApplicationContext context: Context,
    private val nominatimRepository: NominatimRepository,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher,
) : GetCurrentLocationAddress, GetCurrentLocationCoordinate {

    private val appLocationManager: AppLocationManager =
        AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.LOCATION_MANAGER)

    private val mutableGeoCodeFlow: MutableStateFlow<LocationGeoCodeState?> = MutableStateFlow(null)
    override val geoCodeFlow = mutableGeoCodeFlow.asStateFlow()

    private val mutableCurrentLocationFlow: MutableStateFlow<CurrentLocationState?> = MutableStateFlow(null)
    override val currentLocationFlow = mutableCurrentLocationFlow.asStateFlow()

    override suspend fun invoke(): LocationGeoCodeState {
        return when (val currentLocation = getCurrentLocation()) {
            is CurrentLocationState.Success -> {
                val geoCode = reverseGeoCode(currentLocation.latitude, currentLocation.longitude)
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
                launch(dispatcher) {
                    val result = reverseGeoCode(currentLocation.latitude, currentLocation.longitude)
                    mutableGeoCodeFlow.emit(result)
                }
            }
        }
        return currentLocation
    }

    private suspend fun getCurrentLocation(): CurrentLocationState {
        mutableCurrentLocationFlow.emit(CurrentLocationState.Loading())
        if (!appLocationManager.isPermissionGranted) {
            val state = CurrentLocationState.Failure(FeatureType.LOCATION_PERMISSION)
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
            CurrentLocationState.Failure(FeatureType.LOCATION_SERVICE)
        }
        mutableCurrentLocationFlow.emit(result)
        return result
    }

    private suspend fun reverseGeoCode(latitude: Double, longitude: Double): LocationGeoCodeState {
        val result = nominatimRepository.reverseGeoCode(latitude, longitude).fold(onSuccess = { address ->
            LocationGeoCodeState.Success(latitude, longitude, address.simpleDisplayName, address.country)
        }, onFailure = {
            LocationGeoCodeState.Failure(FailedReason.REVERSE_GEOCODE_ERROR)
        })
        return result
    }
}


interface CurrentLocationUseCase {}