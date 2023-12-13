package io.github.pknujsp.weatherwizard.core.ui.notification

import android.annotation.SuppressLint
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.ui.R
import kotlin.reflect.KClass


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

    private fun createNotification(notificationType: NotificationType, context: Context): NotificationCompat.Builder {
        createNotificationChannel(notificationType)

        return NotificationCompat.Builder(context, notificationType.channelId).apply {
            setSmallIcon(io.github.pknujsp.weatherwizard.core.common.R.mipmap.ic_launcher_foreground)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
    }

    fun cancelNotification(notificationType: NotificationType) {
        if (notificationManager.activeNotifications.any { it.id == notificationType.notificationId }) {
            notificationManager.cancel(notificationType.notificationId)
        }
    }

    fun isActiveNotification(notificationType: NotificationType): Boolean =
        notificationManager.activeNotifications.any { it.id == notificationType.notificationId }


    fun getRefreshPendingIntent(context: Context, notificationType: NotificationType, flags: Int, cls: KClass<*>): PendingIntent {
        return PendingIntent.getBroadcast(context,
            pendingIntentRequestFactory.requestId(notificationType::class),
            Intent(context, cls.java).apply {
                action = ""
            },
            flags)
    }

    fun createForegroundNotification(context: Context, notificationType: NotificationType): ForegroundInfo {
        val notification =
            createNotification(notificationType, context).setSmallIcon(io.github.pknujsp.weatherwizard.core.common.R.mipmap.ic_launcher)
                .setContentText(context.getString(notificationType.contentText))
                .setContentTitle(context.getString(notificationType.contentTitle)).setPriority(notificationType.importance).setSilent(true)
                .build()

        return ForegroundInfo(notificationType.notificationId, notification)
    }

    @SuppressLint("MissingPermission")
    fun notifyNotification(notificationType: NotificationType, context: Context, entity: NotificationViewState) {
        val notificationBulder = createNotification(notificationType, context)

        notificationBulder.apply {
            setCustomBigContentView(entity.bigContentRemoteViews)
            setWhen(0)
            setOngoing(notificationType.ongoing)
            setSilent(notificationType.silent)

            entity.icon?.let {
                setSmallIcon(it)
            } ?: run {
                setSmallIcon(io.github.pknujsp.weatherwizard.core.common.R.mipmap.ic_launcher)
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                setContent(entity.smallContentRemoteViews)
                setCustomContentView(entity.smallContentRemoteViews)
            } else {
                setCustomContentView(entity.bigContentRemoteViews)
            }
        }

        NotificationManagerCompat.from(context).notify(notificationType.notificationId, notificationBulder.build())
    }

    @SuppressLint("MissingPermission")
    fun notifyLoadingNotification(notificationType: NotificationType, context: Context) {
        val notificationBulder = createNotification(notificationType, context)

        notificationBulder.setSmallIcon(io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_refresh)
            .setContent(RemoteViews(context.packageName, R.layout.view_loading)).setWhen(0).setSilent(true)

        NotificationManagerCompat.from(context).notify(notificationType.notificationId, notificationBulder.build())
    }
}