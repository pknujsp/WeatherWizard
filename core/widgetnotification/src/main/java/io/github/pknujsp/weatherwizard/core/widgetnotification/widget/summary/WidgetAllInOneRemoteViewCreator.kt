package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.summary

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator

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

            createBaseView(context,
                RemoteViewCreator.ContainerType.WIDGET).apply {
                createHeaderView(this, header)
                addView(R.id.remote_views_content_container, content)
            }
        }
    }


    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        return RemoteViews(context.packageName, R.layout.summary_weather_widget).let {
            createBaseView(context,
             RemoteViewCreator.ContainerType.WIDGET).apply {
                addView(R.id.remote_views_content_container, it)
            }
        }
    }
}