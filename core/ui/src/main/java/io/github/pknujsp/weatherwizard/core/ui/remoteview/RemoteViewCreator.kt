package io.github.pknujsp.weatherwizard.core.ui.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.remoteviews.NotificationViewState
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.ui.R
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator.Companion.lastUpdatedTimeFormat
import java.time.format.DateTimeFormatter

interface RemoteViewCreator {
    companion object {
        const val NOTIFICATION = 0
        const val WIDGET = 1

        val lastUpdatedTimeFormat = DateTimeFormatter.ofPattern("MM.dd E HH:mm")
    }

    fun createBaseView(context: Context, containerType: Int) =
        RemoteViews(context.packageName, if (containerType == NOTIFICATION) R.layout.view_base_notification else R.layout.view_base_widget)
}

abstract class DefaultRemoteViewCreator : RemoteViewCreator {
    abstract fun createSampleView(context: Context, units: CurrentUnits): RemoteViews

    fun createHeaderView(baseView: RemoteViews, remoteViewUiModel: RemoteViewUiModel): RemoteViews = baseView.apply {
        setTextViewText(R.id.address, remoteViewUiModel.address)
        setTextViewText(R.id.last_updated, remoteViewUiModel.lastUpdated.format(lastUpdatedTimeFormat))
    }


}