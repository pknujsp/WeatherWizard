package io.github.pknujsp.weatherwizard.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.feature.widget.WidgetManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class WidgetStarterImpl(
    private val widgetRepository: WidgetRepository
) : WidgetStarter {

    private suspend fun startWidget(context: Context) {
        val widgets = widgetRepository.getAll().firstOrNull()
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