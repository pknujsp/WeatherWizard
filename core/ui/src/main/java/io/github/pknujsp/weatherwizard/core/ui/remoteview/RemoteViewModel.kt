package io.github.pknujsp.weatherwizard.core.ui.remoteview

import androidx.lifecycle.ViewModel
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

abstract class RemoteViewModel {

    val viewModelScope: CoroutineScope
        get() {
            val scope: CoroutineScope? = this.getTag(JOB_KEY)
            if (scope != null) {
                return scope
            }
            return setTagIfAbsent(
                JOB_KEY,
                CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            )
        }

    internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
        override val coroutineContext: CoroutineContext = context

        override fun close() {
            coroutineContext.cancel()
        }
    }
}

private const val JOB_KEY = "RemoteViewModelCoroutineScope.JOB_KEY"