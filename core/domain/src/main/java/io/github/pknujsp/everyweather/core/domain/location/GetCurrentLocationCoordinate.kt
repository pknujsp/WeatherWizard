package io.github.pknujsp.everyweather.core.domain.location

import io.github.pknujsp.everyweather.core.common.StatefulFeature
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

interface GetCurrentLocationCoordinate : CurrentLocationUseCase {
    val currentLocationFlow: StateFlow<CurrentLocationState?>

    suspend operator fun invoke(loadAddress: Boolean): CurrentLocationState
}

sealed interface CurrentLocationState {
    val time: LocalDateTime

    class Loading : CurrentLocationState {
        override val time: LocalDateTime = LocalDateTime.now()
    }

    data class Failure(val reason: StatefulFeature, override val time: LocalDateTime = LocalDateTime.now()) : CurrentLocationState

    data class Success(
        val latitude: Double,
        val longitude: Double,
        override val time: LocalDateTime = LocalDateTime.now(),
    ) : CurrentLocationState
}
