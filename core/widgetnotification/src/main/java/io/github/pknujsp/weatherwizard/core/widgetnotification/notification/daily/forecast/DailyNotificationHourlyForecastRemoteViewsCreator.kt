package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.forecast

import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.mock.MockDataGenerator
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.RemoteViewsMockGenerator.Companion.header
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.DailyNotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.addViewSafely
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DailyNotificationHourlyForecastRemoteViewsCreator :
    DailyNotificationRemoteViewsCreator<DailyNotificationForecastRemoteViewUiModel>() {

    override fun createSmallContentView(model: DailyNotificationForecastRemoteViewUiModel, header: Header, context: Context): RemoteViews {
        val contentView = RemoteViews(context.packageName, R.layout.notification_daily_forecast_small).apply {
            createHourlyForecastView(model, context, 6)
        }
        return createBaseView(context, RemoteViewCreator.ContainerType.NOTIFICATION_SMALL).apply {
            createHeaderView(this, header)
            addViewSafely(R.id.remote_views_content_container, contentView)
        }
    }

    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        val hourRange = 0..8
        val dateRange = 0..4

        val hourlyForecast = MockDataGenerator.hourlyForecastEntity.run {

            items.subList(hourRange.first, hourRange.last).map { item ->
                val calendar = ZonedDateTime.parse(item.dateTime.value)

                DailyNotificationForecastRemoteViewUiModel.HourlyForecast(temperature = item.temperature.convertUnit(units.temperatureUnit)
                    .toString(), weatherIcon = item.weatherCondition.value.dayWeatherIcon, dateTime = calendar.hour.toString())
            }
        }

        val dailyForecast = MockDataGenerator.dailyForecastEntity.run {
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d E")
            dayItems.subList(dateRange.first, dateRange.last).map { item ->
                DailyNotificationForecastRemoteViewUiModel.DailyForecast(temperature = "${item.minTemperature.convertUnit(units.temperatureUnit)} / ${
                    item.maxTemperature.convertUnit(units.temperatureUnit)
                }",
                    weatherIcons = item.items.map { item -> item.weatherCondition.value.dayWeatherIcon },
                    date = dateFormatter.format(ZonedDateTime.parse(item.dateTime.value)))
            }
        }
        return createBigContentView(DailyNotificationForecastRemoteViewUiModel(hourlyForecast, dailyForecast), header, context)
    }

    override fun createBigContentView(
        model: DailyNotificationForecastRemoteViewUiModel, header: Header, context: Context
    ): RemoteViews {
        val contentView = RemoteViews(context.packageName, R.layout.notification_daily_forecast_big).apply {
            createHourlyForecastView(model, context)
            createDailyForecastView(model, context)
        }
        return createBaseView(context, RemoteViewCreator.ContainerType.NOTIFICATION_BIG).apply {
            createHeaderView(this, header)
            addViewSafely(R.id.remote_views_content_container, contentView)
        }
    }


    private fun RemoteViews.createHourlyForecastView(
        model: DailyNotificationForecastRemoteViewUiModel, context: Context, count: Int = model.hourlyForecast.size
    ) {
        model.hourlyForecast.take(count).forEach {
            addView(R.id.hourly_forecast, RemoteViews(context.packageName, R.layout.view_hourly_forecast_item).apply {
                setTextViewText(R.id.time, it.dateTime)
                setImageViewResource(R.id.weather_icon, it.weatherIcon)
                setTextViewText(R.id.temperature, it.temperature)
            })
        }
    }


    private fun RemoteViews.createDailyForecastView(
        model: DailyNotificationForecastRemoteViewUiModel, context: Context
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