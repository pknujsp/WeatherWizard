package io.github.pknujsp.everyweather.core.widgetnotification.remoteview

import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationAddress
import io.github.pknujsp.everyweather.core.domain.location.LocationGeoCodeState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNotNull

abstract class RemoteViewModel(
    protected val getCurrentLocationUseCase: GetCurrentLocationAddress,
) {

    protected suspend fun getCurrentLocation() = callbackFlow {
        if (getCurrentLocationUseCase.geoCodeFlow.value == null) {
            getCurrentLocationUseCase()
        }
        getCurrentLocationUseCase.geoCodeFlow.filterNotNull().collect {
            send(parseCurrentLocationResult(it))
            this.cancel()
        }
    }

    private fun parseCurrentLocationResult(
        locationGeoCodeState: LocationGeoCodeState
    ): CurrentLocationResult = when (locationGeoCodeState) {
        is LocationGeoCodeState.Success -> CurrentLocationResult.Success(
            locationGeoCodeState.latitude,
            locationGeoCodeState.longitude,
            locationGeoCodeState.address,
        )

        is LocationGeoCodeState.Failure -> CurrentLocationResult.Failure(locationGeoCodeState.reason)
    }

    protected sealed interface CurrentLocationResult {

        class Success(
            val latitude: Double,
            val longitude: Double,
            val address: String,
        ) : CurrentLocationResult

        class Failure(
            val reason: StatefulFeature
        ) : CurrentLocationResult

    }
}