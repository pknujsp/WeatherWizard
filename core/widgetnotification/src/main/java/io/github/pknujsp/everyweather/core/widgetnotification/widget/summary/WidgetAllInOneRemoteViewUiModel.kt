package io.github.pknujsp.everyweather.core.widgetnotification.widget.summary

import androidx.annotation.DrawableRes
import io.github.pknujsp.everyweather.core.model.RemoteViewUiModel

data class WidgetAllInOneRemoteViewUiModel(
    val currentWeather: CurrentWeather,
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>,
) : RemoteViewUiModel {
    data class CurrentWeather(
        val temperature: String,
        val feelsLikeTemperature: String,
        val weatherIcon: Int,
    )

    data class HourlyForecast(
        val temperature: String,
        @DrawableRes val weatherIcon: Int,
        val dateTime: String,
    )

    data class DailyForecast(
        val temperature: String,
        @DrawableRes val weatherIcons: List<Int>,
        val date: String,
    )
}
