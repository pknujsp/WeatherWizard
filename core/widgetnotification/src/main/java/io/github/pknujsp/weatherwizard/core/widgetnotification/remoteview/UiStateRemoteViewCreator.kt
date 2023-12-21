package io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview

import android.app.PendingIntent
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.resource.R

object UiStateRemoteViewCreator : RemoteViewCreator {

    enum class ViewSizeType(@LayoutRes val id: Int) {
        SMALL(R.layout.small_view_feature_state), BIG(R.layout.big_view_feature_state)
    }

    fun createView(
        context: Context,
        @StringRes title: Int,
        @StringRes alertMessage: Int,
        @StringRes actionMessage: Int,
        containerType: RemoteViewCreator.ContainerType,
        viewSizeType: ViewSizeType = ViewSizeType.BIG,
        pendingIntent: PendingIntent? = null,
        visibilityOfCompleteButton: Boolean = false,
        visibilityOfActionButton: Boolean = true,
    ): RemoteViews = RemoteViews(context.packageName, viewSizeType.id).run {
        if (viewSizeType == ViewSizeType.BIG) {
            setTextViewText(R.id.title, context.getString(title))
        }
        if (!visibilityOfCompleteButton) {
            setViewVisibility(R.id.complete_button, View.GONE)
        }
        if (!visibilityOfActionButton) {
            setViewVisibility(R.id.action_button, View.GONE)
        }

        setTextViewText(R.id.alert_message, context.getString(alertMessage))
        setTextViewText(R.id.action_button, context.getString(actionMessage))
        setOnClickPendingIntent(R.id.action_button, pendingIntent)

        createBaseView(context, containerType, false).also {
            it.addViewSafely(R.id.remote_views_content_container, this)
        }
    }

    fun createView(
        context: Context,
        failedReason: FailedReason,
        containerType: RemoteViewCreator.ContainerType,
        viewSizeType: ViewSizeType = ViewSizeType.BIG,
        pendingIntent: PendingIntent? = null,
        visibilityOfCompleteButton: Boolean = false,
        visibilityOfActionButton: Boolean = true,
    ): RemoteViews = failedReason.run {
        createView(context,
            title,
            message,
            action,
            containerType,
            viewSizeType,
            pendingIntent,
            visibilityOfCompleteButton,
            visibilityOfActionButton)
    }
}