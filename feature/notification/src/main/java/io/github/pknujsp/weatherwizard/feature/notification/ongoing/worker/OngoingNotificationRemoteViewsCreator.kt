package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.model.OngoingNotificationRemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.feature.notification.remoteview.NotificationRemoteViewsCreator

class OngoingNotificationRemoteViewsCreator : NotificationRemoteViewsCreator<OngoingNotificationRemoteViewUiModel>() {
    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        val contentView = RemoteViews(context.packageName, R.layout.notification_ongoing_small).apply {
            setImageViewResource(R.id.weather_icon, io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_sun)
            setTextViewText(R.id.temperature,
                TemperatureValueType(16.0, TemperatureUnit.Celsius).convertUnit(units.temperatureUnit).toString())
            setTextViewText(R.id.feels_like_temperature,
                TemperatureValueType(16.0, TemperatureUnit.Celsius).convertUnit(units.temperatureUnit).toString())
        }
        return createBaseView(context, RemoteViewCreator.ContainerType.NOTIFICATION_SMALL).apply {
            addView(R.id.remote_views_content_container, contentView)
        }
    }

    override fun createSmallContentView(model: OngoingNotificationRemoteViewUiModel, header: Header, context: Context): RemoteViews {
        val contentView = RemoteViews(context.packageName, R.layout.notification_ongoing_small).apply {
            model.apply {
                setImageViewResource(R.id.weather_icon, currentWeather.weatherIcon)
                setTextViewText(R.id.temperature, currentWeather.temperature)
                setTextViewText(R.id.feels_like_temperature, currentWeather.feelsLikeTemperature)
            }
        }
        return createBaseView(context, RemoteViewCreator.ContainerType.NOTIFICATION_SMALL).apply {
            createHeaderView(this, header)
            addView(R.id.remote_views_content_container, contentView)
        }
    }

    override fun createBigContentView(model: OngoingNotificationRemoteViewUiModel, header: Header, context: Context): RemoteViews {
        val contentView = RemoteViews(context.packageName, R.layout.notification_ongoing_big).apply {
            model.apply {
                setImageViewResource(R.id.weather_icon, currentWeather.weatherIcon)
                setTextViewText(R.id.temperature, currentWeather.temperature)
                setTextViewText(R.id.feels_like_temperature, currentWeather.feelsLikeTemperature)
                createHourlyForecastView(model, context)
            }
        }
        return createBaseView(context, RemoteViewCreator.ContainerType.NOTIFICATION_BIG).apply {
            createHeaderView(this, header)
            addView(R.id.remote_views_content_container, contentView)
        }
    }

    private fun RemoteViews.createHourlyForecastView(model: OngoingNotificationRemoteViewUiModel, context: Context) {
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