package io.github.pknujsp.weatherwizard.core.model

sealed interface DBEntityState<out T : DBEntityModel> {
    data class Exists<out T : DBEntityModel>(val data: T) : DBEntityState<T>

    data object NotExists : DBEntityState<Nothing>
}