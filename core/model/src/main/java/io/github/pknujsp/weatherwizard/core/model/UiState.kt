package io.github.pknujsp.weatherwizard.core.model

import io.github.pknujsp.core.annotation.KBindFunc
import io.github.pknujsp.weatherwizard.core.common.StatefulFeature
import kotlinx.serialization.Serializable

@KBindFunc
sealed interface UiState<out T> {
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val exception: Throwable) : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Failure(val statefulFeature: StatefulFeature) : UiState<Nothing>
}

@Serializable
@KBindFunc
sealed interface VarState<out T> {
    fun valueNotNull(): T

    @Serializable
    data object Uninitialized : VarState<Nothing> {
        override fun valueNotNull(): Nothing = throw NullPointerException("Value is not initialized.")
    }

    @Serializable
    data class Initialized<out T>(val data: T) : VarState<T> {
        override fun valueNotNull(): T = data
    }
}