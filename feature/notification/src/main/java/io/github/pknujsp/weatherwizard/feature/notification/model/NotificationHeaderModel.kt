package io.github.pknujsp.weatherwizard.feature.notification.model

import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.UiModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

abstract class NotificationHeaderModel : UiModel {
    abstract val state: WeatherResponseState
    abstract val updatedTime: ZonedDateTime

    val updatedTimeText: String get() = updatedTime.format(dateTimeFormatter)

    private companion object {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M.d EEE HH:mm")
    }

}