package io.github.pknujsp.weatherwizard.core.model

import io.github.pknujsp.core.annotation.KBindFunc

@KBindFunc
sealed interface UiState<out T> {
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val exception: Throwable) : UiState<Nothing>
    object Loading : UiState<Nothing>
}

@KBindFunc
sealed interface VarState<out T> {
    data object Uninitialized : VarState<Nothing>
    data class Initialized<out T>(val data: T) : VarState<T>
}