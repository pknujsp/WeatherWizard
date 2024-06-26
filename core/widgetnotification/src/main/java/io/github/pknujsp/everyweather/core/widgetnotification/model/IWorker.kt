package io.github.pknujsp.everyweather.core.widgetnotification.model

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import io.github.pknujsp.everyweather.core.FeatureStateManager
import io.github.pknujsp.everyweather.core.FeatureStateManagerImpl
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.NotificationType
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.everyweather.core.common.manager.AppNotificationManager

interface IWorker {
    val name: String
    val requiredFeatures: Array<FeatureType>
}

interface AppComponentService {
    val featureStateManager: FeatureStateManager

    companion object Wake {
        private const val TAG = "AppComponentService"
        private const val wakeLockDuration = 30_000L

        fun acquireWakeLock(context: Context): PowerManager.WakeLock {
            return (context.getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "$TAG:${System.currentTimeMillis()}",
            ).apply {
                acquire(wakeLockDuration)
            }
        }
    }
}

abstract class AppComponentCoroutineService<T : ComponentServiceArgument>(
    private val context: Context,
    params: WorkerParameters,
    private val iWorker: IWorker,
) : CoroutineWorker(context, params), AppComponentService {
    override val featureStateManager: FeatureStateManager by lazy { FeatureStateManagerImpl() }
    open val isRequiredForegroundService: Boolean = true

    protected val appNotificationManager: AppNotificationManager by lazy {
        AppComponentManagerFactory.getManager(context, AppNotificationManager::class)
    }

    override suspend fun doWork(): Result {
        val wakeLock = AppComponentService.acquireWakeLock(context)
        if (isRequiredForegroundService) {
            setForeground(createForegroundInfo())
        }
        Log.d(this::class.simpleName, "$wakeLock : ${wakeLock.isHeld}")

        val result =
            try {
                doWork(context, ComponentServiceAction.toInstance(inputData.keyValueMap).argument as T)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.success()
            } finally {
                wakeLock.release()
                Log.d(this::class.simpleName, "$wakeLock : ${wakeLock.isHeld}")
            }

        return result
    }

    protected abstract suspend fun doWork(
        context: Context,
        argument: T,
    ): Result

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = appNotificationManager.createForegroundNotification(NotificationType.WORKING)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(10, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            ForegroundInfo(10, notification)
        }
    }
}

abstract class AppComponentBackgroundService<T : ComponentServiceArgument>(
    protected val context: Context,
) : AppComponentService {
    override val featureStateManager: FeatureStateManager by lazy { FeatureStateManagerImpl() }

    suspend fun run(argument: T): Result<Unit> {
        val wakeLock = AppComponentService.acquireWakeLock(context)
        Log.d(this::class.simpleName, "$wakeLock : ${wakeLock.isHeld}")

        val result =
            try {
                doWork(argument)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.success(Unit)
            } finally {
                wakeLock.release()
                Log.d(this::class.simpleName, "$wakeLock : ${wakeLock.isHeld}")
            }

        return result
    }

    protected abstract suspend fun doWork(argument: T): Result<Unit>
}
