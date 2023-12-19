package io.github.pknujsp.weatherwizard.feature.componentservice

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.AppNotificationManager
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : LifecycleService() {

    @Inject @CoDispatcher(CoDispatcherType.DEFAULT) lateinit var dispatcher: CoroutineDispatcher

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

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("InlinedApi")
    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationService", "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("NotificationService", "onDestroy")
    }

}