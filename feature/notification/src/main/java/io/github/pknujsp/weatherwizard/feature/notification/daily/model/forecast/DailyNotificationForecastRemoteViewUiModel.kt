package io.github.pknujsp.weatherwizard.feature.notification.daily.model.forecast

import androidx.annotation.DrawableRes
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel

class DailyNotificationForecastRemoteViewUiModel(
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>
) : RemoteViewUiModel {

    data class HourlyForecast(
        val temperature: String,
        @DrawableRes val weatherIcon: Int,
        val dateTime: String
    )

    data class DailyForecast(
        val temperature: String,
        val weatherIcons: List<Int>,
        val date: String
    )
}