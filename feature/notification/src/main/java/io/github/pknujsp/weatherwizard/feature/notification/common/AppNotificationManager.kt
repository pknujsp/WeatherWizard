package io.github.pknujsp.weatherwizard.feature.notification.common

import android.annotation.SuppressLint
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationType
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewsEntity
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationReceiver


class AppNotificationManager(context: Context) {
    private val notificationManager: android.app.NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

    private fun createNotificationChannel(notificationType: NotificationType) {
        notificationType.run {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId, channelName, IMPORTANCE_HIGH).also {
                    it.description = channelDescription
                    it.lockscreenVisibility = VISIBILITY_PUBLIC
                    it.importance = importance
                }

                notificationManager.createNotificationChannel(channel)
            }
        }

    }

    fun createNotification(notificationType: NotificationType, context: Context): NotificationCompat.Builder {
        createNotificationChannel(notificationType)

        /**
        val clickIntent = Intent(context, MainActivity::class.java)
        clickIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(context, System.currentTimeMillis().toInt(), clickIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
         */

        val builder = NotificationCompat.Builder(context, notificationType.channelId).apply {
            setSmallIcon(io.github.pknujsp.weatherwizard.core.common.R.mipmap.ic_launcher_foreground)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
        return builder
    }

    fun cancelNotification(notificationType: NotificationType) {
        val activeNotifications = notificationManager.activeNotifications
        for (activeNotification in activeNotifications) {
            if (activeNotification.id == notificationType.notificationId) {
                notificationManager.cancel(activeNotification.id)
                break
            }
        }
    }

    fun isActiveNotification(notificationType: NotificationType): Boolean {
        val activeNotifications = notificationManager.activeNotifications
        for (activeNotification in activeNotifications) {
            if (activeNotification.id == notificationType.notificationId) {
                return true
            }
        }
        return false
    }

    fun createRefreshPendingIntent(context: Context, notificationType: NotificationType): PendingIntent {
        return PendingIntent.getBroadcast(context, notificationType.notificationId, Intent(context, OngoingNotificationReceiver::class.java)
            .apply {
                action = ""
            }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun createForegroundNotification(context: Context, notificationType: NotificationType): ForegroundInfo {
        val notification = createNotification(notificationType,
            context).setSmallIcon(io.github.pknujsp.weatherwizard.core.common.R.mipmap.ic_launcher)
            .setContentText(context.getString(notificationType.contentText))
            .setContentTitle(context.getString(notificationType.contentTitle))
            .setPriority(notificationType.importance)
            .setSilent(true)
            .build()

        return ForegroundInfo(notificationType.notificationId, notification)
    }

    @SuppressLint("MissingPermission")
    fun notifyNotification(notificationType: NotificationType, context: Context, entity: RemoteViewsEntity) {
        val notificationBulder = createNotification(notificationType, context)

        notificationBulder.setSmallIcon(entity.smallIcon)
            .setSubText(entity.subText)
            .setCustomContentView(entity.smallContentRemoteViews)
            .setCustomBigContentView(entity.bigContentRemoteViews)
            .setOnlyAlertOnce(true).setWhen(0)

        if (notificationType == NotificationType.ONGOING) {
            notificationBulder.setOngoing(true)
        }

        NotificationManagerCompat.from(context).notify(notificationType.notificationId, notificationBulder.build())
    }

}