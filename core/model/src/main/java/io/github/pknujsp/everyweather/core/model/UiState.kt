package io.github.pknujsp.everyweather.core.model

import io.github.pknujsp.core.annotation.KBindFunc
import kotlinx.serialization.Serializable

@KBindFunc
sealed interface UiState<out T> {
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val exception: Throwable) : UiState<Nothing>
    data object Loading : UiState<Nothing>
}

@Serializable
@KBindFunc
sealed interface VarState<out T> {
    fun valueNotNull(): T

    @Serializable
    data object Uninitialized : VarState<Nothing> {
        override fun valueNotNull() = error("Uninitialized")
    }

    @Serializable
    data class Initialized<out T>(val data: T) : VarState<T> {
        override fun valueNotNull(): T = data
    }
}