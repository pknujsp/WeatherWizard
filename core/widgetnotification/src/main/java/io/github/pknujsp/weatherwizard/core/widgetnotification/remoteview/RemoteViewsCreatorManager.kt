package io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview

import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.OngoingNotificationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.forecast.DailyNotificationHourlyForecastRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.OngoingNotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.DailyNotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.dailyforecastcomparison.WidgetDailyForecastComparisonRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.hourlyforecastcomparison.WidgetHourlyForecastComparisonRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.summary.WidgetAllInOneRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.timehourlyforecast.WidgetTimeHourlyForecastRemoteViewCreator

object RemoteViewsCreatorManager {
    inline fun <reified T : DailyNotificationRemoteViewsCreator<RemoteViewUiModel>> getByDailyNotificationType(
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
        WidgetType.ALL_IN_ONE -> WidgetAllInOneRemoteViewCreator() as T
        WidgetType.TIME_HOURLY_FORECAST -> WidgetTimeHourlyForecastRemoteViewCreator() as T
        WidgetType.DAILY_FORECAST_COMPARISON -> WidgetDailyForecastComparisonRemoteViewCreator() as T
        WidgetType.HOURLY_FORECAST_COMPARISON -> WidgetHourlyForecastComparisonRemoteViewCreator() as T
    }
}