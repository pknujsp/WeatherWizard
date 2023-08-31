package io.github.pknujsp.weatherwizard.core.model

import io.github.pknujsp.core.annotation.KBindFunc

@KBindFunc
sealed interface UiState<out T> {
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val exception: Throwable) : UiState<Nothing>
    object Loading : UiState<Nothing>
}

@KBindFunc
sealed class DataState<out T : Number, R>(val value: Int, var size: Int = 1) {
    data class Success(val result: Int) : DataState<Int, String>(result)
    class Error(value: Int) : DataState<Int, String>(value)
    object Loading : DataState<Int, String>(2)
}