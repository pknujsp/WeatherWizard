package io.github.pknujsp.weatherwizard.feature.notification.manager

import android.app.Service
import android.content.Intent
import android.os.IBinder
import io.github.pknujsp.weatherwizard.core.common.NotificationType

class FakeNotificationService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appNotificationManager = AppNotificationManager(applicationContext)
        startForeground(NotificationType.WORKING.notificationId,
            appNotificationManager.createForegroundNotification(applicationContext, NotificationType.WORKING))

        val service = Intent(applicationContext, NotificationService::class.java).apply {
            action = NotificationService.ACTION_START_NOTIFICATION_SERVICE
        }
        startService(service)

        stopForeground(NotificationType.WORKING.notificationId)
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

}