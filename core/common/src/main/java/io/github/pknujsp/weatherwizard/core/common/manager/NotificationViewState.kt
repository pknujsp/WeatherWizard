package io.github.pknujsp.weatherwizard.core.common.manager

import android.app.PendingIntent
import android.widget.RemoteViews
import androidx.core.graphics.drawable.IconCompat
import io.github.pknujsp.weatherwizard.core.resource.R

data class NotificationViewState(
    val success: Boolean,
    val notificationType: NotificationType,
    val smallContentRemoteViews: RemoteViews? = null,
    val bigContentRemoteViews: RemoteViews? = null,
    val smallFailedContentRemoteViews: RemoteViews? = null,
    val bigFailedContentRemoteViews: RemoteViews? = null,
    val icon: IconCompat? = null,
    val refreshPendingIntent: PendingIntent
) {
    init {
        if (success) {
            smallContentRemoteViews?.setOnClickPendingIntent(R.id.action_button, refreshPendingIntent)
            bigContentRemoteViews?.setOnClickPendingIntent(R.id.action_button, refreshPendingIntent)
        } else {
            smallFailedContentRemoteViews?.setOnClickPendingIntent(R.id.complete_button, refreshPendingIntent)
            bigFailedContentRemoteViews?.setOnClickPendingIntent(R.id.complete_button, refreshPendingIntent)
        }
    }
}