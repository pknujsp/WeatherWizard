package io.github.pknujsp.weatherwizard.feature.widget.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.ui.remoteview.SampleViewCreator

interface RemoteViewCreator<T : UiModel> : SampleViewCreator {
    fun createContentView(model: T, context: Context): RemoteViews
}