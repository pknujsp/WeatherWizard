package io.github.pknujsp.weatherwizard.widget

import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class WidgetStarterImpl(
    private val widgetRepository: WidgetRepository,
    private val widgetManager: WidgetManager
) : WidgetStarter {

    private suspend fun startWidget(context: Context) {
        val widgets = widgetRepository.getAll()
    }

    override suspend fun start(context: Context) {
        supervisorScope {
            launch {
                startWidget(context)
            }
        }
    }

}


interface WidgetStarter {
    suspend fun start(context: Context)
}