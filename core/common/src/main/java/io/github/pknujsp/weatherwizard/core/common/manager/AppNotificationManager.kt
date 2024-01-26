package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.resource.R


private class AppNotificationManagerImpl(private val context: Context) : AppNotificationManager {
    private val notificationManager: android.app.NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

    private companion object {
        val mainActivityIntent = Intent().apply {
            val packageName = "io.github.pknujsp.weathernet"
            val className = "io.github.pknujsp.weatherwizard.feature.main.MainActivity"
            setClassName(packageName, className)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        const val MAIN_ACTIVITY_REQUEST_CODE = 235634623
    }

    private fun createNotificationChannel(context: Context, notificationType: NotificationType) {
        notificationType.run {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId, context.getString(channelName), importance).also {
                    it.description = context.getString(channelDescription)
                    it.lockscreenVisibility = VISIBILITY_PUBLIC
                }

                notificationManager.createNotificationChannel(channel)
            }
        }

    }

    private fun createNotification(
        notificationType: NotificationType, context: Context, isOngoing: Boolean = notificationType.ongoing
    ): NotificationCompat.Builder {
        createNotificationChannel(context, notificationType)

        return NotificationCompat.Builder(context, notificationType.channelId).apply {
            setSmallIcon(R.drawable.weatherwizard_icon_logo)
            setOngoing(isOngoing)
            setSilent(notificationType.silent)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setWhen(System.currentTimeMillis())
            priority = notificationType.priority
        }
    }

    override fun cancelNotification(notificationType: NotificationType) {
        if (notificationManager.activeNotifications.any { it.id == notificationType.notificationId }) {
            notificationManager.cancel(notificationType.notificationId)
        }
    }

    override fun isActiveNotification(notificationType: NotificationType): Boolean =
        notificationManager.activeNotifications.any { it.id == notificationType.notificationId }


    override fun createForegroundNotification(notificationType: NotificationType): Notification {
        return createNotification(notificationType, context).apply {
            setContentText(context.getString(notificationType.contentText))
            setContentTitle(context.getString(notificationType.contentTitle))
        }.build()
    }

    @SuppressLint("MissingPermission")
    override fun notifyNotification(notificationType: NotificationType, entity: ExtendedNotification) {
        val notificationBulder = createNotification(notificationType, context).apply {
            if (entity.icon != null) {
                setSmallIcon(entity.icon)
            } else {
                setSmallIcon(R.drawable.weatherwizard_icon_logo)
            }
            setContentIntent(PendingIntent.getActivity(context,
                MAIN_ACTIVITY_REQUEST_CODE,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            setStyle(NotificationCompat.DecoratedCustomViewStyle())
            setCustomBigContentView(if (entity.success) entity.bigContentRemoteViews else entity.bigFailedContentRemoteViews)
            setCustomContentView(if (entity.success) entity.smallContentRemoteViews else entity.smallFailedContentRemoteViews)
        }

        NotificationManagerCompat.from(context).notify(notificationType.notificationId, notificationBulder.build())
    }

    @SuppressLint("MissingPermission")
    override fun notifyLoadingNotification(notificationType: NotificationType) {
        val notificationBulder = createNotification(notificationType, context, false)
        notificationBulder.setSmallIcon(R.drawable.ic_refresh).setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(RemoteViews(context.packageName, R.layout.view_loading_notification))
        NotificationManagerCompat.from(context).notify(notificationType.notificationId, notificationBulder.build())
    }
}

interface AppNotificationManager : AppComponentManager {
    companion object : AppComponentManagerInitializer {
        private var instance: AppNotificationManager? = null

        override fun getInstance(context: Context): AppNotificationManager = synchronized(this) {
            instance ?: AppNotificationManagerImpl(context).also { instance = it }
        }
    }

    fun cancelNotification(notificationType: NotificationType)
    fun isActiveNotification(notificationType: NotificationType): Boolean
    fun createForegroundNotification(notificationType: NotificationType): Notification
    fun notifyNotification(notificationType: NotificationType, entity: ExtendedNotification)
    fun notifyLoadingNotification(notificationType: NotificationType)
}

class ExtendedNotification(
    val success: Boolean,
    val icon: IconCompat? = null,
    val smallContentRemoteViews: RemoteViews? = null,
    val bigContentRemoteViews: RemoteViews? = null,
    val smallFailedContentRemoteViews: RemoteViews? = null,
    val bigFailedContentRemoteViews: RemoteViews? = null
)