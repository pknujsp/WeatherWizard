package io.github.pknujsp.weatherwizard.core.ui.notification

import android.app.PendingIntent
import android.widget.RemoteViews
import androidx.core.graphics.drawable.IconCompat
import io.github.pknujsp.weatherwizard.core.model.R
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType

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
            smallContentRemoteViews?.setOnClickPendingIntent(io.github.pknujsp.weatherwizard.core.ui.R.id.refresh_button,
                refreshPendingIntent)
            bigContentRemoteViews?.setOnClickPendingIntent(io.github.pknujsp.weatherwizard.core.ui.R.id.refresh_button,
                refreshPendingIntent)
        } else {
            failedContentRemoteViews?.setOnClickPendingIntent(io.github.pknujsp.weatherwizard.core.ui.R.id.refresh_button,
                refreshPendingIntent)
        }
    }
}