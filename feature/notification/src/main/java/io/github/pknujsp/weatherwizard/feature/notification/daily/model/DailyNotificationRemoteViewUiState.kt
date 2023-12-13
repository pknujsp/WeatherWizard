package io.github.pknujsp.weatherwizard.feature.notification.daily.model

import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewUiState
import io.github.pknujsp.weatherwizard.core.ui.remoteview.DefaultRemoteViewCreator
import java.time.ZonedDateTime

data class DailyNotificationRemoteViewUiState(
    override val isSuccessful: Boolean,
    override val model: WeatherResponseEntity? = null,
    override val address: String? = null,
    override val lastUpdated: ZonedDateTime? = null,
    val notificationIconType: NotificationIconType? = null,
    val notificationType: DailyNotificationType
) : RemoteViewUiState<WeatherResponseEntity> {
    val header get() = if (isSuccessful) DefaultRemoteViewCreator.Header(address!!, lastUpdated!!) else null
}