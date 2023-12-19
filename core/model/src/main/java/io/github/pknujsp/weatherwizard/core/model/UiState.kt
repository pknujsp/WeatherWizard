package io.github.pknujsp.weatherwizard.core.model

import io.github.pknujsp.core.annotation.KBindFunc
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason

@KBindFunc
sealed interface UiState<out T> {
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val exception: Throwable) : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Failure(val featureType: FeatureType) : UiState<Nothing>
}


@KBindFunc
sealed interface VarState<out T> {
    fun valueNotNull(): T

    data object Uninitialized : VarState<Nothing> {
        override fun valueNotNull(): Nothing = throw NullPointerException("Value is not initialized.")
    }

    data class Initialized<out T>(val data: T) : VarState<T> {
        override fun valueNotNull(): T = data
    }
}

sealed interface ProcessState {
    data object Idle : ProcessState
    data object Running : ProcessState
    data object Succeed : ProcessState
    data class Failed(val reason: FailedReason) : ProcessState
}