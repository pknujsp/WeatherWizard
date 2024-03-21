package io.github.pknujsp.everyweather.core.widgetnotification.notification.ongoing.model

import androidx.annotation.DrawableRes
import io.github.pknujsp.everyweather.core.model.RemoteViewUiModel

class OngoingNotificationRemoteViewUiModel(
    val currentWeather: CurrentWeather,
    val hourlyForecast: List<HourlyForecast>,
) : RemoteViewUiModel {
    data class CurrentWeather(
        val temperature: String,
        val feelsLikeTemperature: String,
        @DrawableRes val weatherIcon: Int,
    )

    data class HourlyForecast(
        val temperature: String,
        @DrawableRes val weatherIcon: Int,
        val dateTime: String,
    )
}
