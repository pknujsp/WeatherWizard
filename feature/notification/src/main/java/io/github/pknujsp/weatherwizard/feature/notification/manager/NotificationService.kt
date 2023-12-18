package io.github.pknujsp.weatherwizard.feature.notification.manager

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.NotificationAction
import io.github.pknujsp.weatherwizard.feature.notification.daily.DailyNotificationService
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : LifecycleService() {

    @Inject @CoDispatcher(CoDispatcherType.DEFAULT) lateinit var dispatcher: CoroutineDispatcher
    @Inject lateinit var ongoingNotificationService: OngoingNotificationService
    @Inject lateinit var dailyNotificationService: DailyNotificationService

    private val appNotificationManager: AppNotificationManager by lazy { AppNotificationManager(applicationContext) }

    companion object {
        const val ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE"
        const val ACTION_STOP_NOTIFICATION_SERVICE = "ACTION_STOP_NOTIFICATION_SERVICE"
        const val ACTION_PROCESS = "NOTIFICATION_SERVICE_ACTION"

        private val running: AtomicBoolean = AtomicBoolean(false)
        val isRunning: Boolean get() = running.get()
    }

    private fun stop() {

    }


    private fun start() {

    }

    private fun process(intent: Intent) {
        Log.d("NotificationService", "onReceive ${intent.extras}, isRunning: $isRunning, service: $this")
        intent.extras?.let {
            lifecycleScope.launch(dispatcher) {
                when (val action = NotificationAction.toInstance(it)) {
                    is NotificationAction.Ongoing -> ongoingNotificationService.start(applicationContext, action.argument)
                    is NotificationAction.Daily -> dailyNotificationService.start(applicationContext, action.argument)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.run {
            when (action) {
                ACTION_START_NOTIFICATION_SERVICE -> {
                    start()
                    stop()
                }

                ACTION_STOP_NOTIFICATION_SERVICE -> stop()
                ACTION_PROCESS -> process(this)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("InlinedApi")
    override fun onCreate() {
        super.onCreate()
        ServiceCompat.startForeground(this@NotificationService,
            NotificationType.WORKING.notificationId,
            appNotificationManager.createForegroundNotification(applicationContext, NotificationType.WORKING),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        Log.d("NotificationService", "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        ServiceCompat.stopForeground(this@NotificationService, ServiceCompat.STOP_FOREGROUND_REMOVE)
        Log.d("NotificationService", "onDestroy")
    }

}