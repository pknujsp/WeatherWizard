package io.github.pknujsp.weatherwizard.core.ui.feature

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.ui.R
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator

class FeatureStateRemoteViewCreator : RemoteViewCreator {

    fun createView(context: Context, featureType: FeatureType, containerType: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_feature_state).let {
            it.setTextViewText(R.id.title, context.getString(featureType.title))
            it.setTextViewText(R.id.alert_message, context.getString(featureType.alertMessage))
            it.setTextViewText(R.id.action_button, context.getString(featureType.actionMessage))
            it.setOnClickPendingIntent(R.id.action_button, featureType.getPendintIntent(context))

            RemoteViewCreator.createBaseView(context, containerType).apply {
                addView(R.id.remote_views_root_container, it)
            }
        }
    }
}