package io.github.pknujsp.weatherwizard.feature.widget.worker

import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewModel
import javax.inject.Inject

class DeleteWidgetRemoteViewModel @Inject constructor(
    private val widgetRepository: WidgetRepository,
) : RemoteViewModel() {

    suspend fun deleteWidgets(appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            widgetRepository.delete(appWidgetId)
        }
    }

}