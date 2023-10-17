package io.github.pknujsp.weatherwizard.feature.notification.common

import android.annotation.SuppressLint
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationType
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewsEntity
import io.github.pknujsp.weatherwizard.feature.notification.R
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
        if (notificationManager.activeNotifications.any { it.id == notificationType.notificationId }) {
            notificationManager.cancel(notificationType.notificationId)
        }
    }

    fun isActiveNotification(notificationType: NotificationType): Boolean =
        notificationManager.activeNotifications.any { it.id == notificationType.notificationId }


    fun getRefreshPendingIntent(context: Context, notificationType: NotificationType, flags: Int): PendingIntent {
        return PendingIntent.getBroadcast(context, notificationType.notificationId, Intent(context, OngoingNotificationReceiver::class.java)
            .apply {
                action = ""
            }, flags)
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

        notificationBulder.apply {
            setSmallIcon(entity.smallIcon)
            setSubText(entity.subText)
            setCustomBigContentView(entity.bigContentRemoteViews)
            setOnlyAlertOnce(true)
            setWhen(0)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                setContent(entity.smallContentRemoteViews)
                setCustomContentView(entity.smallContentRemoteViews)
            } else {
                setCustomContentView(entity.bigContentRemoteViews)
            }

            if (notificationType == NotificationType.ONGOING) {
                setOngoing(true)
            }
        }

        NotificationManagerCompat.from(context).notify(notificationType.notificationId, notificationBulder.build())
    }

    @SuppressLint("MissingPermission")
    fun notifyLoadingNotification(notificationType: NotificationType, context: Context) {
        val notificationBulder = createNotification(notificationType, context)

        notificationBulder.setSmallIcon(io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_refresh)
            .setContent(RemoteViews(context.packageName, R.layout.view_loading))
            .setOnlyAlertOnce(true).setWhen(0).setSilent(true)

        NotificationManagerCompat.from(context).notify(notificationType.notificationId, notificationBulder.build())
    }
}