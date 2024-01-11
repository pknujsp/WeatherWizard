package io.github.pknujsp.weatherwizard.core.domain.location

import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

interface GetCurrentLocationAddress {
    val geoCodeFlow: StateFlow<LocationGeoCodeState?>
    suspend operator fun invoke(): LocationGeoCodeState
}

sealed interface LocationGeoCodeState {
    val time: LocalDateTime

    data class Success(
        val latitude: Double, val longitude: Double, val address: String, val country: String,
        override val time: LocalDateTime = LocalDateTime.now()
    ) : LocationGeoCodeState

    data class Failure(val reason: FailedReason, override val time: LocalDateTime = LocalDateTime.now()) : LocationGeoCodeState
}