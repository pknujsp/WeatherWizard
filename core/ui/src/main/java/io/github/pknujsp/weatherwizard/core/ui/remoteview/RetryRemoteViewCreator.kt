package io.github.pknujsp.weatherwizard.core.ui.remoteview

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.ui.R

object RetryRemoteViewCreator : RemoteViewCreator {

    fun createView(context: Context, title: String, pendingIntent: PendingIntent, containerType: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_retry).let {
            it.setTextViewText(R.id.title, title)
            it.setTextViewText(R.id.action_button, context.getString(io.github.pknujsp.weatherwizard.core.common.R.string.again))
            it.setOnClickPendingIntent(R.id.action_button, pendingIntent)

            createBaseView(context, containerType, false).apply {
                addView(R.id.remote_views_root_container, it)
            }
        }
    }
}