package io.github.pknujsp.weatherwizard.core.ui.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits

interface RemoteViewCreator {
}

interface DefaultRemoteViewCreator : RemoteViewCreator {
    fun createSampleView(context: Context, units: CurrentUnits): RemoteViews
}