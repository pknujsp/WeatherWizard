package io.github.pknujsp.everyweather.core.domain.location

import io.github.pknujsp.everyweather.core.common.StatefulFeature
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

interface GetCurrentLocationAddress : CurrentLocationUseCase {
    val geoCodeFlow: StateFlow<LocationGeoCodeState?>

    suspend operator fun invoke(): LocationGeoCodeState
}

sealed interface LocationGeoCodeState {
    val time: LocalDateTime

    data class Success(
        val latitude: Double,
        val longitude: Double,
        val address: String,
        val country: String,
        override val time: LocalDateTime = LocalDateTime.now(),
    ) : LocationGeoCodeState

    data class Failure(val reason: StatefulFeature, override val time: LocalDateTime = LocalDateTime.now()) : LocationGeoCodeState
}
