package io.github.pknujsp.weatherwizard.feature.notification.ongoing.model

import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.OngoingNotificationType
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewUiState
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.ui.remoteview.DefaultRemoteViewCreator
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