package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.worker.remoteviews

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.resource.R

class DailyNotificationHourlyForecastRemoteViewsCreator :
    io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.NotificationRemoteViewsCreator<io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.forecast.DailyNotificationForecastRemoteViewUiModel>() {
    override fun createSmallContentView(
        model: io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.forecast.DailyNotificationForecastRemoteViewUiModel,
        header: Header,
        context: Context
    ): RemoteViews {
        return createBigContentView(model, header, context)
    }

    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_daily_forecast_big)
    }

    override fun createBigContentView(
        model: io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.forecast.DailyNotificationForecastRemoteViewUiModel,
        header: Header,
        context: Context
    ): RemoteViews {
        val contentView = RemoteViews(context.packageName, R.layout.notification_daily_forecast_big).apply {
            createHourlyForecastView(model, context)
            createDailyForecastView(model, context)
        }
        return createBaseView(context,
            io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator.ContainerType.NOTIFICATION_BIG).apply {
            createHeaderView(this, header)
            addView(R.id.remote_views_content_container, contentView)
        }
    }


    private fun RemoteViews.createHourlyForecastView(
        model: io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.forecast.DailyNotificationForecastRemoteViewUiModel,
        context: Context
    ) {
        model.hourlyForecast.forEach {
            addView(R.id.hourly_forecast, RemoteViews(context.packageName, R.layout.view_hourly_forecast_item).apply {
                setTextViewText(R.id.time, it.dateTime)
                setImageViewResource(R.id.weather_icon, it.weatherIcon)
                setTextViewText(R.id.temperature, it.temperature)
            })
        }
    }


    private fun RemoteViews.createDailyForecastView(
        model: io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.forecast.DailyNotificationForecastRemoteViewUiModel,
        context: Context
    ) {
        model.dailyForecast.forEach {
            addView(R.id.daily_forecast, RemoteViews(context.packageName, R.layout.view_daily_forecast_item).apply {
                setTextViewText(R.id.date, it.date)
                it.weatherIcons.forEach { icon ->
                    addView(R.id.weather_icons, RemoteViews(context.packageName, R.layout.view_weather_icon_item).apply {
                        setImageViewResource(R.id.weather_icon, icon)
                    })
                }
                setTextViewText(R.id.temperature, it.temperature)
            })
        }
    }

}