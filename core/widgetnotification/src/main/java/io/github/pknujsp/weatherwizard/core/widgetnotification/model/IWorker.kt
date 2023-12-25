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

interface AppComponentService {
    val id: Int
}

abstract class AppComponentCoroutineService<T : ComponentServiceArgument>(
    private val context: Context, params: WorkerParameters, private val iWorker: IWorker,
) : CoroutineWorker(context, params), AppComponentService {

    open val isRequiredForegroundService: Boolean = true
    override val id: Int = iWorker.workerId

    protected val appNotificationManager: AppNotificationManager by lazy { AppNotificationManager(context) }
    private val wakeLockDuration = Duration.ofMinutes(1).toMillis()

    private val wakeLock: PowerManager.WakeLock
        get() = (context.getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
            "AppComponentService::${id}")

    override suspend fun doWork(): Result {
        // wakeLock.acquire(wakeLockDuration)
        if (isRequiredForegroundService) {
            setForeground(createForegroundInfo())
        }
        val result = doWork(context, ComponentServiceAction.toInstance(inputData.keyValueMap).argument as T)

        //  wakeLock.release()
        return result
    }

    protected abstract suspend fun doWork(context: Context, argument: T): Result

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = appNotificationManager.createForegroundNotification(context, NotificationType.WORKING)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(iWorker.workerId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            ForegroundInfo(iWorker.workerId, notification)
        }
    }

}

abstract class AppComponentBackgroundService<T : ComponentServiceArgument>(
    protected val context: Context
) : AppComponentService {

    override val id: Int = this::class.simpleName.hashCode()
    private val wakeLockDuration = Duration.ofSeconds(30).toMillis()

    private val wakeLock: PowerManager.WakeLock
        get() = (context.getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
            "AppComponentService::${id}")

    suspend fun run(argument: T): Result<Unit> {
        //  wakeLock.acquire(wakeLockDuration)
        val result = doWork(argument)
        //  wakeLock.release()
        return result
    }

    protected abstract suspend fun doWork(argument: T): Result<Unit>
}