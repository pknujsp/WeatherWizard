package io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview

import io.github.pknujsp.weatherwizard.core.common.enum.IEnum
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.OngoingNotificationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.worker.remoteviews.DailyNotificationHourlyForecastRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.worker.OngoingNotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.summary.SummaryRemoteViewCreator

object RemoteViewsCreatorManager {
    inline fun <reified C : NotificationRemoteViewsCreator<RemoteViewUiModel>> getByNotificationType(
        notificationType: IEnum
    ): C = when (notificationType) {
        DailyNotificationType.FORECAST -> DailyNotificationHourlyForecastRemoteViewsCreator() as C
        OngoingNotificationType.CURRENT_HOURLY_FORECAST -> OngoingNotificationRemoteViewsCreator() as C
        else -> throw IllegalArgumentException("Unknown notification type: $notificationType")
    }

    inline fun <reified C : WidgetRemoteViewsCreator<RemoteViewUiModel>> getByWidgetType(
        widgetType: WidgetType
    ): C = when (widgetType) {
        WidgetType.ALL_IN_ONE -> SummaryRemoteViewCreator() as C
        else -> throw IllegalArgumentException("Unknown widget type: $widgetType")
    }
}