package io.github.pknujsp.weatherwizard.feature.notification.common

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.UiModel

interface NotificationRemoteViewsCreator<T : UiModel> {
    fun createSmallContentView(model: T, context: Context): RemoteViews
    fun createBigContentView(model: T, context: Context): RemoteViews
}