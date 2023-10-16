package io.github.pknujsp.weatherwizard.feature.notification.daily.remoteviews.hourlyforecast

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.notification.daily.hourlyforecast.DailyNotificationHourlyForecastUiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.feature.notification.R
import io.github.pknujsp.weatherwizard.feature.notification.common.NotificationRemoteViewsCreator

class DailyNotificationHourlyForecastRemoteViewsCreator : NotificationRemoteViewsCreator<DailyNotificationHourlyForecastUiModel> {
    override fun createSmallContentView(model: DailyNotificationHourlyForecastUiModel, context: Context): RemoteViews {
        return createBigContentView(model, context)
    }

    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_daily_hourly_forecast).also { container ->
            DailyNotificationHourlyForecastUiModel.createSample(units).forEach { item ->
                RemoteViews(context.packageName, R.layout.view_hourly_forecast_item).let { itemView ->
                    itemView.setTextViewText(R.id.time, item.dateTime)
                    itemView.setImageViewResource(R.id.weather_icon, item.weatherIcon)
                    itemView.setTextViewText(R.id.temperature, item.temperature)

                    container.addView(R.id.hourly_forecast, itemView)
                }
            }
        }
    }

    override fun createBigContentView(model: DailyNotificationHourlyForecastUiModel, context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_daily_hourly_forecast).apply {
            model.apply {
                setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)
                createHourlyForecastView(model, context)
            }
        }
    }


    private fun RemoteViews.createHourlyForecastView(model: DailyNotificationHourlyForecastUiModel, context: Context) {
        model.hourlyForecast.forEach {
            addView(R.id.hourly_forecast, RemoteViews(context.packageName, R.layout.view_hourly_forecast_item).apply {
                setTextViewText(R.id.time, it.dateTime)
                setImageViewResource(R.id.weather_icon, it.weatherIcon)
                setTextViewText(R.id.temperature, it.temperature)
            })
        }
    }

}