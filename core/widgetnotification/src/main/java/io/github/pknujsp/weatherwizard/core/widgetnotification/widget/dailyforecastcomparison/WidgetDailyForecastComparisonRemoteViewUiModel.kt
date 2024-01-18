package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.dailyforecastcomparison

import androidx.annotation.DrawableRes
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

class WidgetDailyForecastComparisonRemoteViewUiModel(
    val weatherProvider: WeatherProvider,
    val dailyForecast: List<DailyForecast>,
) : RemoteViewUiModel {

    class DailyForecast(
        val temperature: String, @DrawableRes val weatherIcons: List<Int>, val date: String
    )
}