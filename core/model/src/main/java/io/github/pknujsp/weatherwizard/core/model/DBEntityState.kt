package io.github.pknujsp.weatherwizard.core.model

sealed interface DBEntityState<out T> {
    data class Exists<out T>(val data: T) : DBEntityState<T>

    data object NotExists : DBEntityState<Nothing>
}