package io.github.pknujsp.weatherwizard.feature.widget

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.widget.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.widget.summary.SummaryRemoteViewCreator

class WidgetManager {
    companion object {
        const val WIDGET_TYPE = "widget_type"
    }

    inline fun <reified C : RemoteViewCreator<out UiModel>> remoteViewCreator(
        widgetType: WidgetType
    ): C =
        when (widgetType) {
            WidgetType.SUMMARY -> SummaryRemoteViewCreator() as C
            else -> throw IllegalArgumentException("Unknown widget type: $widgetType")
        }
}