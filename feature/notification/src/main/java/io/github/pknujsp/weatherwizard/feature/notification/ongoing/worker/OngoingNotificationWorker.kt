package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.text.TextPaint
import android.util.TypedValue
import androidx.core.graphics.drawable.IconCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.permission.PermissionType
import io.github.pknujsp.weatherwizard.core.common.permission.checkSelfPermission
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.feature.notification.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.feature.notification.worker.INotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID


@HiltWorker
class OngoingNotificationWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val remoteViewsModel: OngoingNotificationRemoteViewModel
) : CoroutineWorker(context, params) {
    private val appNotificationManager = AppNotificationManager(context)

    companion object : INotificationWorker {
        override val name: String get() = "OngoingNotificationWorker"
        override val id: UUID get() = UUID.fromString(name)
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !context.checkSelfPermission(PermissionType.POST_NOTIFICATIONS)) {
                Result.success()
            }

            appNotificationManager.notifyLoadingNotification(NotificationType.ONGOING, context)
            val result = remoteViewsModel.load()
            result.onSuccess {
                it.refreshPendingIntent = appNotificationManager.getRefreshPendingIntent(context,
                    NotificationType.ONGOING, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                val remoteViewsCreator = OngoingNotificationRemoteViewsCreator()
                val smallContentRemoteViews = remoteViewsCreator.createSmallContentView(it, context)
                val bigContentRemoteViews = remoteViewsCreator.createBigContentView(it, context)

                val entity = RemoteViewUiModel(true,
                    smallContentRemoteViews,
                    bigContentRemoteViews,
                    "${it.address} • ${it.time}",
                    smallIcon = if (it.iconType == NotificationIconType.TEMPERATURE) createTemperatureIcon(it.currentTemperature) else null,
                    smallIconId = it.currentWeather.weatherIcon)
                appNotificationManager.notifyNotification(NotificationType.ONGOING, context, entity)
            }
            Result.success()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return appNotificationManager.createForegroundNotification(context, NotificationType.WORKING)
    }

    private fun createTemperatureIcon(temperature: String): IconCompat {
        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 19f,
            context.resources.displayMetrics)
        val textRect = Rect()

        val textPaint = TextPaint().apply {
            color = Color.WHITE
            typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            textScaleX = 0.88f
            isAntiAlias = true
            setTextSize(textSize)
            getTextBounds(temperature, 0, temperature.length, textRect)
            style = Paint.Style.FILL
        }

        val iconSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f,
            context.resources.displayMetrics).toInt()
        val iconBitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(iconBitmap)

        val x = canvas.width / 2f
        val y = canvas.height / 2f + textRect.height() / 2f

        canvas.drawText(temperature, x, y, textPaint)
        textPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        canvas.drawText(temperature, x, y, textPaint)

        return IconCompat.createWithBitmap(iconBitmap)
    }
}