package io.github.pknujsp.weatherwizard.feature.notification.daily.worker.remoteviews

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationType
import io.github.pknujsp.weatherwizard.feature.notification.common.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.notification.daily.worker.remoteviews.hourlyforecast.DailyNotificationHourlyForecastRemoteViewsCreator

class RemoteViewsCreatorManager {
    companion object {
        inline fun <reified C : NotificationRemoteViewsCreator<out UiModel>> createRemoteViewsCreator(
            notificationType:
            DailyNotificationType
        ): C =
            when (notificationType) {
                DailyNotificationType.HOURLY_FORECAST -> DailyNotificationHourlyForecastRemoteViewsCreator() as C
                else -> throw IllegalArgumentException("Unknown notification type: $notificationType")
            }
    }
}