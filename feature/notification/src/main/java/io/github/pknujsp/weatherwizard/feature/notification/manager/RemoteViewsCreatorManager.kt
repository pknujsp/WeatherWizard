package io.github.pknujsp.weatherwizard.feature.notification.manager

import io.github.pknujsp.weatherwizard.core.common.enum.IEnum
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.OngoingNotificationType
import io.github.pknujsp.weatherwizard.feature.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.notification.daily.worker.remoteviews.DailyNotificationHourlyForecastRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker.OngoingNotificationRemoteViewsCreator

object RemoteViewsCreatorManager {
    inline fun <reified C : NotificationRemoteViewsCreator<out UiModel>> createRemoteViewsCreator(
        notificationType: IEnum
    ): C = when (notificationType) {
        DailyNotificationType.FORECAST -> DailyNotificationHourlyForecastRemoteViewsCreator() as C
        OngoingNotificationType.CURRENT_HOURLY_FORECAST -> OngoingNotificationRemoteViewsCreator() as C
        else -> throw IllegalArgumentException("Unknown notification type: $notificationType")
    }
}