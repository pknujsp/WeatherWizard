package io.github.pknujsp.weatherwizard.feature.notification.ongoing.model

import androidx.annotation.DrawableRes
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel

class OngoingNotificationRemoteViewUiModel(
    val currentWeather: CurrentWeather,
    val hourlyForecast: List<HourlyForecast>,
) : RemoteViewUiModel {

    data class CurrentWeather(
        val temperature: String, val feelsLikeTemperature: String, @DrawableRes val weatherIcon: Int
    )

    data class HourlyForecast(
        val temperature: String, @DrawableRes val weatherIcon: Int, val dateTime: String
    )

}