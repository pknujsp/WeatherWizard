package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.dailyforecastcomparison

import io.github.pknujsp.weatherwizard.core.data.widget.SavedWidgetContentState
import io.github.pknujsp.weatherwizard.core.model.mapper.UiModelMapper
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WidgetDailyForecastComparisonRemoteViewUiModelMapper :
    UiModelMapper<SavedWidgetContentState.Success, WidgetDailyForecastComparisonRemoteViewUiModel> {
    override fun mapToUiModel(
        model: SavedWidgetContentState.Success, units: CurrentUnits
    ): WidgetDailyForecastComparisonRemoteViewUiModel {
        return model.let {
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d E", Locale.getDefault())
            val items = it.entities.map { entity ->
                val dailyForecast = entity.toEntity<DailyForecastEntity>().dayItems.subList(0, 5).map { item ->
                    WidgetDailyForecastComparisonRemoteViewUiModel.DailyForecast(temperature = "${item.minTemperature.convertUnit(units.temperatureUnit)}/${
                        item.maxTemperature.convertUnit(units.temperatureUnit)
                    }",
                        weatherIcons = item.items.map { dayItem -> dayItem.weatherCondition.value.dayWeatherIcon },
                        date = dateFormatter.format(ZonedDateTime.parse(item.dateTime.value)))
                }
                WidgetDailyForecastComparisonRemoteViewUiModel.Item(
                    weatherProvider = entity.weatherProvider,
                    dailyForecast = dailyForecast,
                )
            }
            
            WidgetDailyForecastComparisonRemoteViewUiModel(items)
        }
    }

}