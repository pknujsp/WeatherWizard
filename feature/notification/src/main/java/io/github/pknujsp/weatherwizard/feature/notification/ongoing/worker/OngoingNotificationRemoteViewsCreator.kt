package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.notification.ongoing.OngoingNotificationUiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.feature.notification.R
import io.github.pknujsp.weatherwizard.feature.notification.remoteview.NotificationRemoteViewsCreator

class OngoingNotificationRemoteViewsCreator : NotificationRemoteViewsCreator<OngoingNotificationUiModel> {
    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_ongoing_small).apply {
            setImageViewResource(R.id.weather_icon, io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_sun)
            setTextViewText(R.id.temperature,
                TemperatureValueType(16.0, TemperatureUnit.Celsius).convertUnit(units.temperatureUnit).toString())
            setTextViewText(R.id.feels_like_temperature,
                TemperatureValueType(16.0, TemperatureUnit.Celsius).convertUnit(units.temperatureUnit).toString())
        }
    }

    override fun createSmallContentView(model: OngoingNotificationUiModel, context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_ongoing_small).apply {
            model.apply {
                setImageViewResource(R.id.weather_icon, currentWeather.weatherIcon)
                setTextViewText(R.id.temperature, currentWeather.temperature)
                setTextViewText(R.id.feels_like_temperature, currentWeather.feelsLikeTemperature)
                setTextViewText(R.id.address, address)
                setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)
            }
        }
    }

    override fun createBigContentView(model: OngoingNotificationUiModel, context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_ongoing_big).apply {
            model.apply {
                setImageViewResource(R.id.weather_icon, currentWeather.weatherIcon)
                setTextViewText(R.id.temperature, currentWeather.temperature)
                setTextViewText(R.id.feels_like_temperature, currentWeather.feelsLikeTemperature)
                setTextViewText(R.id.address, address)
                setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)
                createHourlyForecastView(model, context)
            }
        }
    }

    private fun RemoteViews.createHourlyForecastView(model: OngoingNotificationUiModel, context: Context) {
        model.hourlyForecast.forEach {
            RemoteViews(context.packageName, R.layout.view_hourly_forecast_item).apply {
                setTextViewText(R.id.time, it.dateTime)
                setImageViewResource(R.id.weather_icon, it.weatherIcon)
                setTextViewText(R.id.temperature, it.temperature)
            }.also {
                addView(R.id.hourly_forecast, it)
            }
        }
    }
}