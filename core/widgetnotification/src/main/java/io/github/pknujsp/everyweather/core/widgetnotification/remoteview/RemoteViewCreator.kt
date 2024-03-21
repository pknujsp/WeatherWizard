package io.github.pknujsp.everyweather.core.widgetnotification.remoteview

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewCreator.Companion.lastUpdatedTimeFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

interface RemoteViewCreator {
    companion object {
        val lastUpdatedTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MM.dd E HH:mm")
    }

    enum class ContainerType(
        @LayoutRes val layoutRes: Int,
    ) {
        WIDGET(R.layout.view_base_widget),
        NOTIFICATION_SMALL(R.layout.small_view_base_notification),
        NOTIFICATION_BIG(R.layout.big_view_base_notification),
    }

    fun createBaseView(
        context: Context,
        containerType: ContainerType,
        visibilityOfHeader: Boolean = true,
    ) = RemoteViews(context.packageName, containerType.layoutRes).apply {
        if (!visibilityOfHeader) {
            setViewVisibility(R.id.header, View.GONE)
        }
    }
}

abstract class DefaultRemoteViewCreator : RemoteViewCreator {
    abstract fun createSampleView(
        context: Context,
        units: CurrentUnits,
    ): RemoteViews

    fun createHeaderView(
        baseView: RemoteViews,
        header: Header,
    ): RemoteViews =
        baseView.apply {
            setTextViewText(R.id.address, header.address)
            setTextViewText(R.id.last_updated, header.lastUpdated.format(lastUpdatedTimeFormat))
        }

    data class Header(
        val address: String,
        val lastUpdated: ZonedDateTime,
    )
}
