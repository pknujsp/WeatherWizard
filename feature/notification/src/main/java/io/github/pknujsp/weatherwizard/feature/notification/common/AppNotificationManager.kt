package io.github.pknujsp.weatherwizard.feature.notification.common

import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationType
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationReceiver


class AppNotificationManager(context: Context) {
    private val notificationManager: android.app.NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

    private fun createNotificationChannel(notificationType: NotificationType) {
        notificationType.run {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId, channelName, IMPORTANCE_HIGH).apply {
                    description = channelDescription
                    lockscreenVisibility = VISIBILITY_PUBLIC
                    importance = importance
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
}