package io.github.pknujsp.weatherwizard.core.network.api

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

internal class MultipleRequestHelper<T : Any>(
) {
    private val requestMap = mutableMapOf<Long, MutableStateFlow<RequestState<T>>>()
    private val mutex = Mutex()

    suspend fun add(requestId: Long): Boolean = mutex.withLock {
        if (requestId in requestMap) {
            false
        } else {
            val newFlow = MutableStateFlow<RequestState<T>>(RequestState.Waiting)
            requestMap[requestId] = newFlow

            CoroutineScope(SupervisorJob()).launch {
                val addedCollector = AtomicBoolean(false)
                newFlow.subscriptionCount.collect {
                    if (!addedCollector.get() && it > 0) {
                        Log.d("MultipleRequestHelper", "added $requestId")
                        addedCollector.getAndSet(true)
                    } else if (addedCollector.get() && it == 0) {
                        mutex.withLock {
                            requestMap.remove(requestId)
                        }
                        Log.d("MultipleRequestHelper", "removed $requestId")
                    }
                }
            }
            true
        }
    }


    suspend fun get(requestId: Long): StateFlow<RequestState<T>>? = mutex.withLock { requestMap[requestId] }

    suspend fun update(requestId: Long, requestState: RequestState<T>) {
        mutex.withLock {
            requestMap[requestId]?.value = requestState
        }
    }
}


internal sealed class RequestState<out T> {
    private val addedTime: Long = System.currentTimeMillis()

    fun isTimeout(cacheMaxTime: Long, now: Long): Boolean = addedTime + cacheMaxTime < now

    data object Waiting : RequestState<Nothing>()

    data class Responsed<T>(
        val response: T,
    ) : RequestState<T>()

    data class Failure(
        val throwable: Throwable,
    ) : RequestState<Nothing>()
}

internal fun <T> RequestState<T>.onResponse(): Result<T> = when (this) {
    is RequestState.Responsed -> Result.success(response)
    is RequestState.Failure -> Result.failure(throwable)
    else -> Result.failure(Throwable("Unknown error"))
}