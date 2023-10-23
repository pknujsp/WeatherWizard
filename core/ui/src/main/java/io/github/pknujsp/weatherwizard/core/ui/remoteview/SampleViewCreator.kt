package io.github.pknujsp.weatherwizard.core.ui.remoteview

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits

interface SampleViewCreator {
    fun createSampleView(context: Context, units: CurrentUnits): RemoteViews
}