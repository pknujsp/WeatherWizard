package io.github.pknujsp.weatherwizard.feature.widget.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.ui.remoteview.DefaultRemoteViewCreator

abstract class WidgetRemoteViewsCreator<T : RemoteViewUiModel> : DefaultRemoteViewCreator() {
    abstract fun createContentView(model: T, context: Context): RemoteViews
}