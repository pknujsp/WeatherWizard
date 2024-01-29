package io.github.pknujsp.everyweather.core.widgetnotification.widget.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.everyweather.core.model.RemoteViewUiModel
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.DefaultRemoteViewCreator

abstract class WidgetRemoteViewsCreator<T : RemoteViewUiModel> : DefaultRemoteViewCreator() {
    abstract fun createContentView(model: T, header: Header, context: Context): RemoteViews
}