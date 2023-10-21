package io.github.pknujsp.weatherwizard.feature.notification.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits

interface NotificationRemoteViewsCreator<T : UiModel> {
    fun createSmallContentView(model: T, context: Context): RemoteViews
    fun createBigContentView(model: T, context: Context): RemoteViews

    fun createSampleView(context: Context, units: CurrentUnits): RemoteViews
}