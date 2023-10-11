package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.notification.NotificationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.GetCurrentWeatherUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetHourlyForecastUseCase
import io.github.pknujsp.weatherwizard.core.model.nominatim.ReverseGeoCodeEntity
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.feature.notification.R
import io.github.pknujsp.weatherwizard.feature.notification.common.AppNotificationManager
import io.github.pknujsp.weatherwizard.feature.notification.common.INotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@HiltWorker
class OngoingNotificationWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val appSettingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository,
    private val nominatimRepository: NominatimRepository,
) : CoroutineWorker(context, params) {

    companion object : INotificationWorker {
        override val name: String = "OngoingNotificationWorker"
        override val id: UUID = UUID.fromString(name)
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            appSettingsRepository.init()
            val units = appSettingsRepository.currentUnits.value
            val notificationInfo = notificationRepository.getOngoingNotificationInfo().getOrNull() ?: return@withContext Result.failure()

            val requestId = System.currentTimeMillis()
            val latitude = notificationInfo.latitude
            val longitude = notificationInfo.longitude
            val weatherProvider = notificationInfo.getWeatherProvider()

            val area = nominatimRepository.reverseGeoCode(latitude, longitude).getOrNull() ?: return@withContext Result.failure()
            val currentWeather =
                getCurrentWeatherUseCase(latitude, longitude, weatherProvider, requestId).getOrNull() ?: return@withContext Result.failure()
            val hourlyForecast =
                getHourlyForecastUseCase(latitude, longitude, weatherProvider, requestId).getOrNull() ?: return@withContext Result.failure()
            notifyNotification(currentWeather, area, units, requestId)
            Result.success()
        }
    }

    @SuppressLint("MissingPermission")
    private fun notifyNotification(currentWeather: CurrentWeatherEntity, area: ReverseGeoCodeEntity, units: CurrentUnits, id: Long) {
        val appNotificationManager = AppNotificationManager(context)
        val notificationBulder = appNotificationManager.createNotification(NotificationType.ONGOING, context)
        val refreshPendingIntent = appNotificationManager.createRefreshPendingIntent(context, NotificationType.ONGOING)

        val notification = notificationBulder.setSmallIcon(io.github.pknujsp.weatherwizard.core.common.R.mipmap.ic_launcher_foreground)
            .setContentText("ongoing notification text")
            .setContentTitle("ongoing notification title")
            .setCustomContentView(createSmallContentView(currentWeather, area, units, id, refreshPendingIntent))
            .setCustomBigContentView(createBigContentView(currentWeather, area, units, id, refreshPendingIntent))
            .setOnlyAlertOnce(true).setWhen(0).setOngoing(true)
            .build()

        NotificationManagerCompat.from(context).notify(NotificationType.ONGOING.notificationId, notification)
    }

    private fun createSmallContentView(
        currentWeather: CurrentWeatherEntity, area: ReverseGeoCodeEntity, units:
        CurrentUnits, id: Long, refreshPendingIntent: PendingIntent
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_ongoing_small).apply {
            currentWeather.apply {
                setImageViewResource(R.id.weather_icon, weatherCondition.value.dayWeatherIcon)
                setTextViewText(R.id.temperature, temperature.convertUnit(units.temperatureUnit).value.toString())
                setTextViewText(R.id.feels_like_temperature, feelsLikeTemperature.convertUnit(units.temperatureUnit).value.toString())
                setTextViewText(R.id.address, area.simpleDisplayName)
                setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)
            }
        }
    }

    private fun createBigContentView(
        currentWeather: CurrentWeatherEntity, area: ReverseGeoCodeEntity, units:
        CurrentUnits, id: Long, refreshPendingIntent: PendingIntent
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_ongoing_big).apply {
            currentWeather.apply {
                setImageViewResource(R.id.weather_icon, weatherCondition.value.dayWeatherIcon)
                setTextViewText(R.id.temperature, temperature.convertUnit(units.temperatureUnit).value.toString())
                setTextViewText(R.id.feels_like_temperature, feelsLikeTemperature.convertUnit(units.temperatureUnit).value.toString())
                setTextViewText(R.id.address, area.simpleDisplayName)
                setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)
            }
        }
    }

}