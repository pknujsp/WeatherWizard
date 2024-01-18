package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.timehourlyforecast

import android.content.Context
import android.widget.RemoteViews
import androidx.core.widget.RemoteViewsCompat.setTextViewText
import io.github.pknujsp.weatherwizard.core.model.mock.MockDataGenerator
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.RemoteViewsMockGenerator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.addViewSafely
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import java.time.ZonedDateTime

class WidgetTimeHourlyForecastRemoteViewCreator : WidgetRemoteViewsCreator<WidgetTimeHourlyForecastRemoteViewUiModel>() {
    override fun createContentView(
        model: WidgetTimeHourlyForecastRemoteViewUiModel, header: Header, context: Context
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_time_hourly_forecast_widget).let { content ->
            model.currentWeather.let {
                val item = RemoteViews(context.packageName, R.layout.view_hourly_forecast_item).apply {
                    setTextViewText(R.id.time, it.dateTime)
                    setImageViewResource(R.id.weather_icon, it.weatherIcon)
                    setTextViewText(R.id.temperature, it.temperature)
                }
                content.addView(R.id.hourly_forecast_row, item)
            }

            model.hourlyForecast.forEach {
                val item = RemoteViews(context.packageName, R.layout.view_hourly_forecast_item).apply {
                    setTextViewText(R.id.time, it.dateTime)
                    setImageViewResource(R.id.weather_icon, it.weatherIcon)
                    setTextViewText(R.id.temperature, it.temperature)
                }
                content.addView(R.id.hourly_forecast_row, item)
            }

            createBaseView(context, RemoteViewCreator.ContainerType.WIDGET).apply {
                createHeaderView(this, header)
                addViewSafely(R.id.remote_views_content_container, content)
            }
        }
    }


    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        val mockModel = WidgetTimeHourlyForecastRemoteViewUiModel(currentWeather = MockDataGenerator.currentWeatherEntity.run {
            WidgetTimeHourlyForecastRemoteViewUiModel.CurrentWeather(temperature.convertUnit(units.temperatureUnit).toString(),
                weatherCondition.value.dayWeatherIcon)
        }, hourlyForecast = MockDataGenerator.hourlyForecastEntity.items.map {
            WidgetTimeHourlyForecastRemoteViewUiModel.HourlyForecast(it.temperature.convertUnit(units.temperatureUnit).toString(),
                it.weatherCondition.value.dayWeatherIcon,
                ZonedDateTime.parse(it.dateTime.value).hour.toString())
        })
        return createContentView(mockModel, RemoteViewsMockGenerator.header, context)
    }
}