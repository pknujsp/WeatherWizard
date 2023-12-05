package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.TypedValue
import androidx.core.graphics.drawable.IconCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStateChecker
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.core.ui.feature.FeatureStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RetryRemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.notification.daily.worker.DailyNotificationWorker
import io.github.pknujsp.weatherwizard.feature.notification.manager.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationUiModelMapper
import io.github.pknujsp.weatherwizard.feature.notification.remoteview.NotificationRemoteViewsCreator


@HiltWorker
class OngoingNotificationWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val remoteViewsModel: OngoingNotificationRemoteViewModel
) : CoroutineWorker(context, params) {
    private val appNotificationManager = AppNotificationManager(context)
    private val featureStateRemoteViewCreator = FeatureStateRemoteViewCreator()
    private val retryRemoteViewCreator = RetryRemoteViewCreator()

    companion object : IWorker {
        override val name: String get() = "OngoingNotificationWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf(FeatureType.NETWORK, FeatureType.POST_NOTIFICATION_PERMISSION)
    }


    override suspend fun doWork(): Result {
        if (!checkFeatureStateAndNotify(DailyNotificationWorker.requiredFeatures)) {
            return Result.success()
        }
        val notificationEntity = remoteViewsModel.loadNotification()

        if (notificationEntity.location.locationType is LocationType.CurrentLocation && !checkFeatureStateAndNotify(arrayOf(FeatureType.LOCATION_PERMISSION,
                FeatureType.LOCATION_SERVICE))) {
            return Result.success()
        }

        val uiModel = remoteViewsModel.load(notificationEntity)
        val creator: NotificationRemoteViewsCreator<UiModel> = RemoteViewsCreatorManager.createRemoteViewsCreator(uiModel.notification.type)
        val retryPendingIntent = appNotificationManager.getRefreshPendingIntent(context,
            NotificationType.ONGOING,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            OngoingNotificationReceiver::class)
        when (uiModel.state) {
            is WeatherResponseState.Success -> {
                val model = OngoingNotificationUiModelMapper().map(uiModel, remoteViewsModel.units)
                model.refreshPendingIntent = retryPendingIntent
                val smallRemoteView = creator.createSmallContentView(model, context)
                val bigRemoteView = creator.createBigContentView(model, context)

                val entity = RemoteViewUiModel(
                    true,
                    smallRemoteView,
                    bigRemoteView,
                    "${uiModel.notification.location.address} • ${uiModel.updatedTimeText}",
                    smallIcon = if (model.iconType == NotificationIconType.TEMPERATURE) createTemperatureIcon(model.currentTemperature) else null,
                    smallIconId = model.currentWeather.weatherIcon,
                )
                appNotificationManager.notifyNotification(NotificationType.ONGOING, context, entity)
            }

            else -> {
                val remoteViewUiModel = RemoteViewUiModel(
                    false,
                    failedContentRemoteViews = retryRemoteViewCreator.createView(context,
                        context.getString(io.github.pknujsp.weatherwizard.core.common.R.string.refresh),
                        retryPendingIntent,
                        RemoteViewCreator.NOTIFICATION),
                )
                appNotificationManager.notifyNotification(NotificationType.ONGOING, context, remoteViewUiModel)
            }

        }

        return Result.success()
    }


    private fun checkFeatureStateAndNotify(featureTypes: Array<FeatureType>): Boolean {
        return when (val state = FeatureStateChecker.checkFeatureState(context, featureTypes)) {
            is FeatureState.Unavailable -> {
                val remoteViews = featureStateRemoteViewCreator.createView(context, state.featureType, RemoteViewCreator.NOTIFICATION)
                val remoteViewUiModel = RemoteViewUiModel(
                    false,
                    failedContentRemoteViews = remoteViews,
                )
                appNotificationManager.notifyNotification(NotificationType.ONGOING, context, remoteViewUiModel)
                false
            }

            else -> true
        }
    }


    override suspend fun getForegroundInfo(): ForegroundInfo {
        return appNotificationManager.createForegroundNotification(context, NotificationType.WORKING)
    }

    private fun createTemperatureIcon(temperature: String): IconCompat {
        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 19f, context.resources.displayMetrics)
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

        val iconSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, context.resources.displayMetrics).toInt()
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