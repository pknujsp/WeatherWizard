package io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview

import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.OngoingNotificationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.forecast.DailyNotificationHourlyForecastRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.OngoingNotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.summary.SummaryRemoteViewCreator

object RemoteViewsCreatorManager {
    inline fun <reified T : NotificationRemoteViewsCreator<RemoteViewUiModel>> getByDailyNotificationType(
        notificationType: DailyNotificationType
    ): T = when (notificationType) {
        DailyNotificationType.FORECAST -> DailyNotificationHourlyForecastRemoteViewsCreator() as T
        else -> throw IllegalArgumentException("Unknown notification type: $notificationType")
    }

    inline fun <reified T : NotificationRemoteViewsCreator<RemoteViewUiModel>> getByOngoingNotificationType(
        notificationType: OngoingNotificationType
    ): T = when (notificationType) {
        OngoingNotificationType.CURRENT_HOURLY_FORECAST -> OngoingNotificationRemoteViewsCreator() as T
        else -> throw IllegalArgumentException("Unknown notification type: $notificationType")
    }

    inline fun <reified T : WidgetRemoteViewsCreator<RemoteViewUiModel>> getByWidgetType(
        widgetType: WidgetType
    ): T = when (widgetType) {
        WidgetType.ALL_IN_ONE -> SummaryRemoteViewCreator() as T
        else -> throw IllegalArgumentException("Unknown widget type: $widgetType")
    }
}