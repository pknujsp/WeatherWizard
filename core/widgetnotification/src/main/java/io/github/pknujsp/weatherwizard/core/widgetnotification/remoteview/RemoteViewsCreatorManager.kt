package io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview

import io.github.pknujsp.weatherwizard.core.common.enum.IEnum
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.OngoingNotificationType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.worker.remoteviews.DailyNotificationHourlyForecastRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.worker.OngoingNotificationRemoteViewsCreator

object RemoteViewsCreatorManager {
    inline fun <reified C : io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.NotificationRemoteViewsCreator<RemoteViewUiModel>> createRemoteViewsCreator(
        notificationType: IEnum
    ): C = when (notificationType) {
        DailyNotificationType.FORECAST -> DailyNotificationHourlyForecastRemoteViewsCreator() as C
        OngoingNotificationType.CURRENT_HOURLY_FORECAST -> OngoingNotificationRemoteViewsCreator() as C
        else -> throw IllegalArgumentException("Unknown notification type: $notificationType")
    }
}