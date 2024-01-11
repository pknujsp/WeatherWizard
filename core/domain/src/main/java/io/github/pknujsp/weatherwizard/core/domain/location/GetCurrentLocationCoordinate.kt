package io.github.pknujsp.weatherwizard.core.domain.location

import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

interface GetCurrentLocationCoordinate {
    val currentLocationFlow: StateFlow<CurrentLocationState?>
    suspend operator fun invoke(loadAddress: Boolean): CurrentLocationState
}


sealed interface CurrentLocationState {
    val time: LocalDateTime

    class Loading : CurrentLocationState {
        override val time: LocalDateTime = LocalDateTime.now()
    }

    data class Failure(val reason: FailedReason, override val time: LocalDateTime = LocalDateTime.now()) : CurrentLocationState
    data class Success(
        val latitude: Double, val longitude: Double, override val time: LocalDateTime = LocalDateTime.now()
    ) : CurrentLocationState
}