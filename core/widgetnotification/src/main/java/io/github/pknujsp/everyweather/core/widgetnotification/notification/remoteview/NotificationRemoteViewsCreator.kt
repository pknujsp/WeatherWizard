package io.github.pknujsp.everyweather.core.widgetnotification.notification.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.everyweather.core.model.RemoteViewUiModel
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.DefaultRemoteViewCreator

abstract class NotificationRemoteViewsCreator<T : RemoteViewUiModel> : DefaultRemoteViewCreator() {
    abstract fun createSmallContentView(
        model: T,
        header: Header,
        context: Context,
    ): RemoteViews

    abstract fun createBigContentView(
        model: T,
        header: Header,
        context: Context,
    ): RemoteViews
}

abstract class DailyNotificationRemoteViewsCreator<T : RemoteViewUiModel> : DefaultRemoteViewCreator() {
    abstract fun createSmallContentView(
        model: T,
        header: Header,
        context: Context,
    ): RemoteViews

    abstract fun createBigContentView(
        model: T,
        header: Header,
        context: Context,
    ): RemoteViews
}
