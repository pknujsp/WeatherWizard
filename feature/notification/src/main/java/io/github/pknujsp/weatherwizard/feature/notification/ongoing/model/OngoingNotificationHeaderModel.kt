package io.github.pknujsp.weatherwizard.feature.notification.ongoing.model

import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.feature.notification.model.NotificationHeaderModel
import java.time.ZonedDateTime

data class OngoingNotificationHeaderModel(
    override val updatedTime: ZonedDateTime, override val state: WeatherResponseState, val notification: OngoingNotificationSettingsEntity
) : NotificationHeaderModel() {
}