package io.github.pknujsp.weatherwizard.core.common.manager

import android.app.PendingIntent
import android.widget.RemoteViews
import androidx.core.graphics.drawable.IconCompat
import io.github.pknujsp.weatherwizard.core.common.R

data class NotificationViewState(
    val success: Boolean,
    val notificationType: NotificationType,
    val smallContentRemoteViews: RemoteViews? = null,
    val bigContentRemoteViews: RemoteViews? = null,
    val failedContentRemoteViews: RemoteViews? = null,
    val icon: IconCompat? = null,
    val refreshPendingIntent: PendingIntent
) {
    init {
        if (success) {
            smallContentRemoteViews?.setOnClickPendingIntent(R.id.action_button, refreshPendingIntent)
            bigContentRemoteViews?.setOnClickPendingIntent(R.id.action_button, refreshPendingIntent)
        } else {
            failedContentRemoteViews?.setOnClickPendingIntent(R.id.action_button, refreshPendingIntent)
        }
    }
}