package io.github.pknujsp.weatherwizard.core.widgetnotification.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.NotificationViewState


class AppNotificationManager(context: Context) {
    private val notificationManager: android.app.NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

    private fun createNotificationChannel(notificationType: NotificationType) {
        notificationType.run {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId, channelName, importance).also {
                    it.description = channelDescription
                    it.lockscreenVisibility = VISIBILITY_PUBLIC
                }

                notificationManager.createNotificationChannel(channel)
            }
        }

    }

    fun createNotification(notificationType: NotificationType, context: Context): NotificationCompat.Builder {
        createNotificationChannel(notificationType)

        return NotificationCompat.Builder(context, notificationType.channelId).apply {
            setSmallIcon(R.mipmap.ic_launcher_foreground)
            setOngoing(notificationType.ongoing)
            setSilent(notificationType.silent)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            priority = notificationType.importance
            setWhen(0)
        }
    }

    fun cancelNotification(notificationType: NotificationType) {
        if (notificationManager.activeNotifications.any { it.id == notificationType.notificationId }) {
            notificationManager.cancel(notificationType.notificationId)
        }
    }

    fun isActiveNotification(notificationType: NotificationType): Boolean =
        notificationManager.activeNotifications.any { it.id == notificationType.notificationId }


    fun createForegroundNotification(context: Context, notificationType: NotificationType): Notification {
        return createNotification(notificationType, context).apply {
            setSmallIcon(R.mipmap.ic_launcher_foreground).setContentText(context.getString(notificationType.contentText))
            setContentTitle(context.getString(notificationType.contentTitle))
        }.build()
    }

    @SuppressLint("MissingPermission")
    fun notifyNotification(notificationType: NotificationType, context: Context, entity: NotificationViewState) {
        val notificationBulder = createNotification(notificationType, context).apply {
            entity.icon?.let {
                setSmallIcon(it)
            } ?: run {
                setSmallIcon(R.mipmap.ic_launcher_foreground)
            }
            setCustomBigContentView(if (entity.success) entity.bigContentRemoteViews else entity.bigFailedContentRemoteViews)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setContent(if (entity.success) entity.smallContentRemoteViews else entity.smallFailedContentRemoteViews)
                setCustomContentView(if (entity.success) entity.smallContentRemoteViews else entity.smallFailedContentRemoteViews)
            } else {
                setCustomContentView(if (entity.success) entity.bigContentRemoteViews else entity.bigFailedContentRemoteViews)
            }
        }

        NotificationManagerCompat.from(context).notify(notificationType.notificationId, notificationBulder.build())
    }

    @SuppressLint("MissingPermission")
    fun notifyLoadingNotification(notificationType: NotificationType, context: Context) {
        val notificationBulder = createNotification(notificationType, context)

        notificationBulder.setSmallIcon(R.drawable.ic_refresh).setContent(RemoteViews(context.packageName, R.layout.view_loading_notification))
            .setCustomContentView(RemoteViews(context.packageName, R.layout.view_loading_notification))
        NotificationManagerCompat.from(context).notify(notificationType.notificationId, notificationBulder.build())
    }
}