package io.github.pknujsp.everyweather.core.widgetnotification.widget.hourlyforecastcomparison

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.everyweather.core.model.RemoteViewUiModel
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

class WidgetHourlyForecastComparisonRemoteViewUiModel(
    val items: List<Item>,
) : RemoteViewUiModel {
    class Item(
        val weatherProvider: WeatherProvider,
        val currentWeather: CurrentWeather,
        val hourlyForecast: List<HourlyForecast>,
    )

    class CurrentWeather(
        val temperature: String,
        @DrawableRes val weatherIcon: Int,
    ) {
        @StringRes val dateTime: Int = io.github.pknujsp.everyweather.core.resource.R.string.now
    }

    class HourlyForecast(
        val temperature: String,
        @DrawableRes val weatherIcon: Int,
        val dateTime: String,
    )
}
