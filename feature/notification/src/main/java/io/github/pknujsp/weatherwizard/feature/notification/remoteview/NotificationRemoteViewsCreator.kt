package io.github.pknujsp.weatherwizard.feature.notification.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.ui.remoteview.DefaultRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator

abstract class NotificationRemoteViewsCreator<T : RemoteViewUiModel> : DefaultRemoteViewCreator() {
    abstract fun createSmallContentView(model: T, context: Context): RemoteViews
    abstract fun createBigContentView(model: T, context: Context): RemoteViews
}