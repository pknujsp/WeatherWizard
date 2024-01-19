package io.github.pknujsp.weatherwizard.core.widgetnotification.model

import io.github.pknujsp.weatherwizard.core.data.widget.SavedWidgetContentState
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.mapper.UiModelMapper
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.OngoingNotificationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.forecast.DailyNotificationForecastUiModelMapper
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.model.mapper.OngoingNotificationRemoteViewUiModelMapper
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.WidgetUiModelMapper
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.dailyforecastcomparison.WidgetDailyForecastComparisonRemoteViewUiModelMapper
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.hourlyforecastcomparison.WidgetHourlyForecastComparisonRemoteViewUiModelMapper
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.summary.WidgetAllInOneRemoteViewUiModelMapper
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.timehourlyforecast.WidgetTimeHourlyForecastRemoteViewUiModelMapper

object RemoteViewUiModelMapperManager {
    fun getByDailyNotificationType(
        notificationType: DailyNotificationType
    ): UiModelMapper<WeatherResponseEntity, out RemoteViewUiModel> = when (notificationType) {
        DailyNotificationType.FORECAST -> DailyNotificationForecastUiModelMapper()
        else -> throw IllegalArgumentException("Unknown notification type: $notificationType")
    }

    fun getByOngoingNotificationType(
        notificationType: OngoingNotificationType
    ): UiModelMapper<WeatherResponseEntity, out RemoteViewUiModel> = when (notificationType) {
        OngoingNotificationType.CURRENT_HOURLY_FORECAST -> OngoingNotificationRemoteViewUiModelMapper()
        else -> throw IllegalArgumentException("Unknown notification type: $notificationType")
    }

    fun getByWidgetType(
        widgetType: WidgetType
    ): WidgetUiModelMapper<SavedWidgetContentState.Success, out RemoteViewUiModel> = when (widgetType) {
        WidgetType.ALL_IN_ONE -> WidgetAllInOneRemoteViewUiModelMapper()
        WidgetType.HOURLY_FORECAST_COMPARISON -> WidgetHourlyForecastComparisonRemoteViewUiModelMapper()
        WidgetType.DAILY_FORECAST_COMPARISON -> WidgetDailyForecastComparisonRemoteViewUiModelMapper()
        WidgetType.TIME_HOURLY_FORECAST -> WidgetTimeHourlyForecastRemoteViewUiModelMapper()
    }
}