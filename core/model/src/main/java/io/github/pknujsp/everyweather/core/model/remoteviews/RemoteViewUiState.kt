package io.github.pknujsp.everyweather.core.model.remoteviews

import java.time.ZonedDateTime

interface RemoteViewUiState<T> {
    val isSuccessful: Boolean
    val address: String?
    val lastUpdated: ZonedDateTime?
    val model: T?
}
