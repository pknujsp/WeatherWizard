package io.github.pknujsp.weatherwizard.feature.widget.activity

import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import javax.inject.Inject

class DeleteWidgetRemoteViewModel @Inject constructor(
    private val widgetRepository: WidgetRepository,
) : io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewModel() {

    suspend fun deleteWidgets(appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            widgetRepository.delete(appWidgetId)
        }
    }

}