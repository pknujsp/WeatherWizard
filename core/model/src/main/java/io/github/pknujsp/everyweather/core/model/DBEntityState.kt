package io.github.pknujsp.everyweather.core.model

sealed interface DBEntityState<out T : Any> {
    data class Exists<out T : Any>(val data: T) : DBEntityState<T>

    data object NotExists : DBEntityState<Nothing>
}
