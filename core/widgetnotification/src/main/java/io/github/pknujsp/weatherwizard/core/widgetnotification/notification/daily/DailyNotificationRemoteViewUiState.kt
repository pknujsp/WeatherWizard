package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily

import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewUiState
import java.time.ZonedDateTime

data class DailyNotificationRemoteViewUiState(
    override val isSuccessful: Boolean,
    override val model: WeatherResponseEntity? = null,
    override val address: String? = null,
    override val lastUpdated: ZonedDateTime? = null,
    val notificationIconType: NotificationIconType? = null,
    val notificationType: DailyNotificationType
) : RemoteViewUiState<WeatherResponseEntity> {
    val header get() = if (isSuccessful) io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator.Header(address!!, lastUpdated!!) else null
}