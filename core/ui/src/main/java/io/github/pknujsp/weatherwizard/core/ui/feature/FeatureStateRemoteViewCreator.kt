package io.github.pknujsp.weatherwizard.core.ui.feature

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.ui.R
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator

class FeatureStateRemoteViewCreator : RemoteViewCreator {

    companion object {
        const val NOTIFICATION = 0
        const val WIDGET = 1
    }

    fun createView(context: Context, featureType: FeatureType, containerType: Int): RemoteViews {
        return RemoteViews(context.packageName,
            if (containerType == WIDGET) R.layout.view_feature_state_widget else R.layout.view_feature_state).apply {
            setTextViewText(R.id.title, context.getString(featureType.title))
            setTextViewText(R.id.alert_message, context.getString(featureType.alertMessage))
            setTextViewText(R.id.action_button, context.getString(featureType.actionMessage))
            setOnClickPendingIntent(R.id.action_button, featureType.getPendintIntent(context))
        }
    }
}