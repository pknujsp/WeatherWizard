package io.github.pknujsp.everyweather.core.widgetnotification.widget.dailyforecastcomparison

import androidx.annotation.DrawableRes
import io.github.pknujsp.everyweather.core.model.RemoteViewUiModel
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

class WidgetDailyForecastComparisonRemoteViewUiModel(
    val items: List<Item>,
) : RemoteViewUiModel {

    class Item(
        val weatherProvider: WeatherProvider,
        val dailyForecast: List<DailyForecast>,
    )

    class DailyForecast(
        val temperature: String, @DrawableRes val weatherIcons: List<Int>, val date: String
    )
}