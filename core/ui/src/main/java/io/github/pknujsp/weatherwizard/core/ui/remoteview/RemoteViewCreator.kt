package io.github.pknujsp.weatherwizard.core.ui.remoteview

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.ui.R
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator.Companion.lastUpdatedTimeFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

interface RemoteViewCreator {
    companion object {
        const val NOTIFICATION = 0
        const val WIDGET = 1

        val lastUpdatedTimeFormat = DateTimeFormatter.ofPattern("MM.dd E HH:mm")
    }

    fun createBaseView(context: Context, containerType: Int, visibilityOfHeader: Boolean = true) = RemoteViews(context.packageName,
        if (containerType == NOTIFICATION) R.layout.view_base_notification else R.layout.view_base_widget).apply {
        if (!visibilityOfHeader) {
            setViewVisibility(R.id.header, View.GONE)
        }
    }

}

abstract class DefaultRemoteViewCreator : RemoteViewCreator {
    abstract fun createSampleView(context: Context, units: CurrentUnits): RemoteViews

    fun createHeaderView(baseView: RemoteViews, header: Header): RemoteViews = baseView.apply {
        setTextViewText(R.id.address, header.address)
        setTextViewText(R.id.last_updated, header.lastUpdated.format(lastUpdatedTimeFormat))
    }

    data class Header(
        val address: String, val lastUpdated: ZonedDateTime
    )
}