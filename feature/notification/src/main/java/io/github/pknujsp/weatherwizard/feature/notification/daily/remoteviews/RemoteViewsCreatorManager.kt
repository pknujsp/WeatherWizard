package io.github.pknujsp.weatherwizard.feature.notification.daily.remoteviews

import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationType
import io.github.pknujsp.weatherwizard.feature.notification.common.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.notification.daily.remoteviews.hourlyforecast.DailyNotificationHourlyForecastRemoteViewsCreator

class RemoteViewsCreatorManager {
    companion object {
        fun createRemoteViewsCreator(notificationType: DailyNotificationType): NotificationRemoteViewsCreator<*> =
            when (notificationType) {
                DailyNotificationType.HOURLY_FORECAST -> DailyNotificationHourlyForecastRemoteViewsCreator()
                else -> throw IllegalArgumentException("Unknown notification type: $notificationType")
            }
    }
}