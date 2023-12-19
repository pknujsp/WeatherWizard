package io.github.pknujsp.weatherwizard.core.widgetnotification.model

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.PowerManager
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.AppNotificationManager
import java.time.Duration

interface IWorker {
    val name: String
    val requiredFeatures: Array<FeatureType>
    val workerId: Int
}


abstract class AppComponentService<T : ComponentServiceArgument>(
    private val context: Context, params: WorkerParameters, private val iWorker: IWorker
) : CoroutineWorker(context, params) {

    protected val appNotificationManager: AppNotificationManager by lazy { AppNotificationManager(context) }
    private val wakeLockDuration = Duration.ofMinutes(1).toMinutes()

    private var wakeLock: PowerManager.WakeLock? = (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
        newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AppComponentService::${iWorker.workerId}").apply {
            acquire(wakeLockDuration)
        }
    }

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())
        val result = doWork(context, ComponentServiceAction.toInstance(inputData.keyValueMap).argument as T)

        wakeLock?.release()
        wakeLock = null
        return result
    }

    abstract suspend fun doWork(context: Context, argument: T): Result

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = appNotificationManager.createForegroundNotification(context, NotificationType.WORKING)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(iWorker.workerId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            ForegroundInfo(iWorker.workerId, notification)
        }
    }

}