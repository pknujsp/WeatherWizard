package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.summary

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.mock.MockDataGenerator
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.RemoteViewsMockGenerator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.addViewSafely
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WidgetAllInOneRemoteViewCreator : WidgetRemoteViewsCreator<WidgetAllInOneRemoteViewUiModel>() {
    override fun createContentView(
        model: WidgetAllInOneRemoteViewUiModel, header: Header, context: Context
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.summary_weather_widget).let { content ->
            model.currentWeather.let { currentWeather ->
                content.setTextViewText(R.id.temperature, currentWeather.temperature)
                content.setTextViewText(R.id.feels_like_temperature, currentWeather.feelsLikeTemperature)
                content.setImageViewResource(R.id.weather_icon, currentWeather.weatherIcon)
            }
            model.hourlyForecast.map {
                RemoteViews(context.packageName, R.layout.view_hourly_forecast_item).apply {
                    setTextViewText(R.id.time, it.dateTime)
                    setImageViewResource(R.id.weather_icon, it.weatherIcon)
                    setTextViewText(R.id.temperature, it.temperature)
                }
            }.let { remoteViews ->
                remoteViews.subList(0, 6).forEach {
                    content.addView(R.id.hourly_forecast_row_1, it)
                }
                remoteViews.subList(6, 12).forEach {
                    content.addView(R.id.hourly_forecast_row_2, it)
                }
            }

            model.dailyForecast.forEach {
                content.addView(R.id.daily_forecast, RemoteViews(context.packageName, R.layout.view_daily_forecast_item).apply {
                    setTextViewText(R.id.date, it.date)
                    it.weatherIcons.forEach { icon ->
                        addView(R.id.weather_icons, RemoteViews(context.packageName, R.layout.view_weather_icon_item).apply {
                            setImageViewResource(R.id.weather_icon, icon)
                        })
                    }
                    setTextViewText(R.id.temperature, it.temperature)
                })
            }

            createBaseView(context, RemoteViewCreator.ContainerType.WIDGET).apply {
                createHeaderView(this, header)
                addViewSafely(R.id.remote_views_content_container, content)
            }
        }
    }


    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        val dateFormatter = DateTimeFormatter.ofPattern("d E", Locale.getDefault())
        val mockModel = WidgetAllInOneRemoteViewUiModel(currentWeather = MockDataGenerator.currentWeatherEntity.run {
            WidgetAllInOneRemoteViewUiModel.CurrentWeather(temperature.convertUnit(units.temperatureUnit).toString(),
                feelsLikeTemperature.convertUnit(units.temperatureUnit).toString(),
                weatherCondition.value.dayWeatherIcon)
        }, hourlyForecast = MockDataGenerator.hourlyForecastEntity.items.map {
            WidgetAllInOneRemoteViewUiModel.HourlyForecast(it.temperature.convertUnit(units.temperatureUnit).toString(),
                it.weatherCondition.value.dayWeatherIcon,
                ZonedDateTime.parse(it.dateTime.value).hour.toString())
        }, dailyForecast = MockDataGenerator.dailyForecastEntity.dayItems.subList(0, 5).map { dayItem ->
            WidgetAllInOneRemoteViewUiModel.DailyForecast("${dayItem.minTemperature.convertUnit(units.temperatureUnit)}/${
                dayItem.maxTemperature.convertUnit(units.temperatureUnit)
            }",
                dayItem.items.map { it.weatherCondition.value.dayWeatherIcon },
                dateFormatter.format(ZonedDateTime.parse(dayItem.dateTime.value)))
        })
        return createContentView(mockModel, RemoteViewsMockGenerator.header, context)
    }
}