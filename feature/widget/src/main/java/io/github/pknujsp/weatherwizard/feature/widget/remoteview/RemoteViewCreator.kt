package io.github.pknujsp.weatherwizard.feature.widget.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits

interface RemoteViewCreator<T : UiModel> {
    fun createContentView(model: T, context: Context): RemoteViews
    fun createSampleView(context: Context, units: CurrentUnits): RemoteViews
}