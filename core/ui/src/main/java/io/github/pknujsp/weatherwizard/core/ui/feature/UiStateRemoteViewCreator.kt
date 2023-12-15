package io.github.pknujsp.weatherwizard.core.ui.feature

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.ui.R
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator

object UiStateRemoteViewCreator : RemoteViewCreator {

    enum class ViewSizeType(@LayoutRes val id: Int) {
        SMALL(R.layout.small_view_feature_state), BIG(R.layout.big_view_feature_state)
    }

    fun createView(
        context: Context,
        @StringRes title: Int,
        @StringRes alertMessage: Int,
        @StringRes actionMessage: Int,
        containerType: Int,
        pendingIntent: PendingIntent,
        viewSizeType: ViewSizeType = ViewSizeType.BIG,
    ): RemoteViews = RemoteViews(context.packageName, viewSizeType.id).run {
        if (viewSizeType == ViewSizeType.BIG) {
            setTextViewText(R.id.title, context.getString(title))
        }

        setTextViewText(R.id.alert_message, context.getString(alertMessage))
        setTextViewText(R.id.action_button, context.getString(actionMessage))
        setOnClickPendingIntent(R.id.action_button, pendingIntent)

        createBaseView(context, containerType, false).also {
            it.addView(R.id.remote_views_root_container, this)
        }
    }

    fun createView(
        context: Context,
        featureType: FeatureType,
        containerType: Int,
        pendingIntent: PendingIntent = featureType.getPendingIntent(context),
        viewSizeType: ViewSizeType = ViewSizeType.BIG,
    ): RemoteViews = featureType.failedReason.run {
        createView(context, title, message, action, containerType, pendingIntent, viewSizeType)
    }
}