package io.github.pknujsp.everyweather.core.widgetnotification.widget.timehourlyforecast

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.everyweather.core.model.RemoteViewUiModel
import io.github.pknujsp.everyweather.core.widgetnotification.R

data class WidgetTimeHourlyForecastRemoteViewUiModel(
    val currentWeather: CurrentWeather,
    val hourlyForecast: List<HourlyForecast>,
) : RemoteViewUiModel {

    class CurrentWeather(
        val temperature: String,
        @DrawableRes val weatherIcon: Int,
    ) {
        @StringRes val dateTime: Int = io.github.pknujsp.everyweather.core.resource.R.string.now
    }

    class HourlyForecast(
        val temperature: String, @DrawableRes val weatherIcon: Int, val dateTime: String
    )
}