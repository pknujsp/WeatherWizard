package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.hourlyforecastcomparison

import android.content.Context
import android.widget.RemoteViews
import androidx.core.widget.RemoteViewsCompat.setTextViewText
import io.github.pknujsp.weatherwizard.core.model.mock.MockDataGenerator
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.RemoteViewsMockGenerator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.addViewSafely
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import java.time.ZonedDateTime

class WidgetHourlyForecastComparisonRemoteViewCreator : WidgetRemoteViewsCreator<WidgetHourlyForecastComparisonRemoteViewUiModel>() {
    override fun createContentView(
        model: WidgetHourlyForecastComparisonRemoteViewUiModel, header: Header, context: Context
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_time_hourly_forecast_widget).let { content ->
            // 날씨 제공사 별로 처리
            model.items.forEach { item ->
                val containerView = RemoteViews(context.packageName, R.layout.view_forecast_container).apply {
                    setTextViewText(R.id.weather_provider_name, item.weatherProvider.title)
                    setImageViewResource(R.id.weather_provider_icon, item.weatherProvider.icon!!)
                }

                item.currentWeather.let {
                    val view = RemoteViews(context.packageName, R.layout.view_hourly_forecast_item).apply {
                        setTextViewText(R.id.time, it.dateTime)
                        setImageViewResource(R.id.weather_icon, it.weatherIcon)
                        setTextViewText(R.id.temperature, it.temperature)
                    }
                    containerView.addView(R.id.forecast_row, view)
                }

                item.hourlyForecast.forEach {
                    val view = RemoteViews(context.packageName, R.layout.view_hourly_forecast_item).apply {
                        setTextViewText(R.id.time, it.dateTime)
                        setImageViewResource(R.id.weather_icon, it.weatherIcon)
                        setTextViewText(R.id.temperature, it.temperature)
                    }
                    containerView.addView(R.id.forecast_row, view)
                }
            }

            createBaseView(context, RemoteViewCreator.ContainerType.WIDGET).apply {
                createHeaderView(this, header)
                addViewSafely(R.id.remote_views_content_container, content)
            }
        }
    }


    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        val currentWeather = MockDataGenerator.currentWeatherEntity.run {
            WidgetHourlyForecastComparisonRemoteViewUiModel.CurrentWeather(temperature.convertUnit(units.temperatureUnit).toString(),
                weatherCondition.value.dayWeatherIcon)
        }
        val hourlyForecast = MockDataGenerator.hourlyForecastEntity.items.map {
            WidgetHourlyForecastComparisonRemoteViewUiModel.HourlyForecast(it.temperature.convertUnit(units.temperatureUnit).toString(),
                it.weatherCondition.value.dayWeatherIcon,
                ZonedDateTime.parse(it.dateTime.value).hour.toString())
        }
        val mockModel = WidgetHourlyForecastComparisonRemoteViewUiModel(WeatherProvider.enums.map {
            WidgetHourlyForecastComparisonRemoteViewUiModel.Item(it, currentWeather, hourlyForecast)
        })
        return createContentView(mockModel, RemoteViewsMockGenerator.header, context)
    }
}