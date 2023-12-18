package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel

abstract class WidgetRemoteViewsCreator<T : RemoteViewUiModel> : io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator() {
    abstract fun createContentView(model: T, header: Header, context: Context): RemoteViews
}