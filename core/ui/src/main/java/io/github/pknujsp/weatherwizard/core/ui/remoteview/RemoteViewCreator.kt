package io.github.pknujsp.weatherwizard.core.ui.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.ui.R

interface RemoteViewCreator {
    companion object {
        const val NOTIFICATION = 0
        const val WIDGET = 1

        fun createBaseView(context: Context, containerType: Int) = RemoteViews(context.packageName,
            if (containerType == NOTIFICATION) R.layout.view_base_notification else R.layout.view_base_widget)
    }
}

interface DefaultRemoteViewCreator : RemoteViewCreator {
    fun createSampleView(context: Context, units: CurrentUnits): RemoteViews
}