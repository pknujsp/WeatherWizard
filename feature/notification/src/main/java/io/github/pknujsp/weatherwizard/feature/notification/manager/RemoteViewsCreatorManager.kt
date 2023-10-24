package io.github.pknujsp.weatherwizard.feature.notification.manager

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.feature.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.notification.daily.worker.remoteviews.DailyNotificationHourlyForecastRemoteViewsCreator

class RemoteViewsCreatorManager {
    companion object {
        inline fun <reified C : NotificationRemoteViewsCreator<out UiModel>> createRemoteViewsCreator(
            notificationType:
            DailyNotificationType
        ): C =
            when (notificationType) {
                DailyNotificationType.FORECAST -> DailyNotificationHourlyForecastRemoteViewsCreator() as C
                else -> throw IllegalArgumentException("Unknown notification type: $notificationType")
            }
    }
}