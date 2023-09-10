package io.github.pknujsp.weatherwizard.core.network.retrofit

import io.github.pknujsp.core.annotation.KBindFunc

@KBindFunc
sealed interface NetworkApiResult<out T> {
    data class Success<out T>(val data: T) : NetworkApiResult<T>

    sealed class Failure(open val exception: Throwable) : NetworkApiResult<Nothing> {
        class ApiError(exception: Throwable) : Failure(exception)
        class NetworkError(exception: Throwable) : Failure(exception)
        class UnknownError(exception: Throwable) : Failure(exception)
    }
}

internal inline fun <reified T> NetworkApiResult<T>.onResult(): Result<T> {
    return when (this) {
        is NetworkApiResult.Success -> Result.success(data)
        is NetworkApiResult.Failure.ApiError -> Result.failure(exception)
        is NetworkApiResult.Failure.NetworkError -> Result.failure(exception)
        is NetworkApiResult.Failure.UnknownError -> Result.failure(exception)
    }
}