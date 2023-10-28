package io.github.pknujsp.weatherwizard.feature.widget.summary

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.widget.R
import io.github.pknujsp.weatherwizard.feature.widget.remoteview.WidgetRemoteViewsCreator

class SummaryRemoteViewCreator : WidgetRemoteViewsCreator<SummaryUiModel> {
    override fun createContentView(model: SummaryUiModel, context: Context): RemoteViews {
        TODO("Not yet implemented")
    }

    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        return RemoteViews(context.packageName, R.layout.summary_weather_widget).let {

            RemoteViewCreator.createBaseView(context, RemoteViewCreator.WIDGET).apply {
                addView(io.github.pknujsp.weatherwizard.core.ui.R.id.remote_views_root_container, it)
            }
        }
    }
}