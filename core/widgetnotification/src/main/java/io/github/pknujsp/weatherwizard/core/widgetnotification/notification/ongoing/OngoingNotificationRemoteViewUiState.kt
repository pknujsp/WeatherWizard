package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing

import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.OngoingNotificationType
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewUiState
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator
import java.time.ZonedDateTime

data class OngoingNotificationRemoteViewUiState(
    override val isSuccessful: Boolean,
    override val model: WeatherResponseEntity? = null,
    override val address: String? = null,
    override val lastUpdated: ZonedDateTime? = null,
    val notificationIconType: NotificationIconType? = null,
    val notificationType: OngoingNotificationType
) : RemoteViewUiState<WeatherResponseEntity> {
    val header get() = if (isSuccessful) DefaultRemoteViewCreator.Header(address!!, lastUpdated!!) else null
}