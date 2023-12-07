package io.github.pknujsp.weatherwizard.core.network.api

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

internal class MultipleRequestHelper<T>(
    private val cacheMaxTime: Duration = Duration.ofMinutes(5),
) {
    private val requestMap = ConcurrentHashMap<Long, MutableStateFlow<RequestState<out T>>>()

    private fun clear() {
        val now = LocalDateTime.now()
        requestMap.filter { it.value.value.isTimeout(cacheMaxTime, now) }.forEach {
            requestMap.remove(it.key)
        }
    }

    fun addRequest(requestId: Long): Boolean {
        clear()

        return if (requestMap.contains(requestId)) {
            false
        } else {
            requestMap[requestId] = MutableStateFlow(RequestState.Waiting)
            true
        }
    }

    fun get(requestId: Long): StateFlow<RequestState<out T>>? = requestMap[requestId]?.asStateFlow()

    fun update(requestId: Long, requestState: RequestState<out T>) {
        requestMap[requestId]?.value = requestState
    }
}


internal sealed class RequestState<T> {
    private val addedTime: LocalDateTime = LocalDateTime.now()

    fun isTimeout(cacheMaxTime: Duration, now: LocalDateTime): Boolean = Duration.between(addedTime, now) > cacheMaxTime

    data object Waiting : RequestState<Nothing>()

    data class Success<T>(
        val response: T,
    ) : RequestState<T>()

    data class Failure(
        val throwable: Throwable,
    ) : RequestState<Nothing>()
}

internal fun <T> RequestState<T>.onResponse(): Result<T> = when (this) {
    is RequestState.Success -> Result.success(response)
    is RequestState.Failure -> Result.failure(throwable)
    else -> Result.failure(Throwable("Unknown error"))
}