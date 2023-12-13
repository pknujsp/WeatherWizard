package io.github.pknujsp.weatherwizard.core.ui.feature

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.ui.R
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator

object UiStateRemoteViewCreator : RemoteViewCreator {

    fun createView(
        context: Context,
        @StringRes title: Int,
        @StringRes alertMessage: Int,
        @StringRes actionMessage: Int,
        containerType: Int,
        pendingIntent: PendingIntent
    ): RemoteViews = RemoteViews(context.packageName, R.layout.view_feature_state).let {
        it.setTextViewText(R.id.title, context.getString(title))
        it.setTextViewText(R.id.alert_message, context.getString(alertMessage))
        it.setTextViewText(R.id.action_button, context.getString(actionMessage))
        it.setOnClickPendingIntent(R.id.action_button, pendingIntent)

        createBaseView(context, containerType, false).apply {
            addView(R.id.remote_views_root_container, it)
        }
    }

    fun createView(
        context: Context,
        featureType: FeatureType,
        containerType: Int,
        pendingIntent: PendingIntent = featureType.getPendingIntent(context),
    ): RemoteViews = featureType.failedReason.run {
        createView(context, title, message, action, containerType, pendingIntent)
    }
}