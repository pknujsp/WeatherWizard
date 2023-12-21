package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator

abstract class NotificationRemoteViewsCreator<T : RemoteViewUiModel> : DefaultRemoteViewCreator() {
    abstract fun createSmallContentView(model: T, header: Header, context: Context): RemoteViews
    abstract fun createBigContentView(model: T, header: Header, context: Context): RemoteViews
}