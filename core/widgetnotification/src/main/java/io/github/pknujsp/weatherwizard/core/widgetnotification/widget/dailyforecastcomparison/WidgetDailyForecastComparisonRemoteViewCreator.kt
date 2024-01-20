package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.dailyforecastcomparison

import android.content.Context
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
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
import java.time.format.DateTimeFormatter
import java.util.Locale

class WidgetDailyForecastComparisonRemoteViewCreator : WidgetRemoteViewsCreator<WidgetDailyForecastComparisonRemoteViewUiModel>() {
    override fun createContentView(
        model: WidgetDailyForecastComparisonRemoteViewUiModel, header: Header, context: Context
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_forecast_comparison_widget).let { content ->
            // 날씨 제공사 별로 처리
            model.items.forEach { item ->
                val containerView = RemoteViews(context.packageName, R.layout.view_forecast_container).apply {
                    setTextViewText(R.id.weather_provider_name, item.weatherProvider.title)
                    setImageViewResource(R.id.weather_provider_icon, item.weatherProvider.icon!!)
                }
                content.addView(R.id.forecast_comparison_column, containerView)

                item.dailyForecast.forEach {
                    containerView.addView(R.id.forecast_row, RemoteViews(context.packageName, R.layout.view_daily_forecast_item).apply {
                        applyDailyForecastItem(it.date, it.temperature)
                        it.weatherIcons.forEach { icon ->
                            addView(R.id.weather_icons, RemoteViews(context.packageName, R.layout.view_weather_icon_item).also { iconView ->
                                iconView.setImageViewResource(R.id.weather_icon, icon)
                            })
                        }
                    })
                }
            }

            createBaseView(context, RemoteViewCreator.ContainerType.WIDGET).apply {
                createHeaderView(this, header)
                addViewSafely(R.id.remote_views_content_container, content)
            }
        }
    }

    private fun RemoteViews.applyDailyForecastItem(date: String, temperature: String) {
        setTextViewText(R.id.date, date)
        setTextViewText(R.id.temperature, temperature)

        setTextViewTextSize(R.id.date, TypedValue.COMPLEX_UNIT_SP, 14f)
        setTextViewTextSize(R.id.temperature, TypedValue.COMPLEX_UNIT_SP, 13f)
    }


    override fun createSampleView(context: Context, units: CurrentUnits): RemoteViews {
        val dateFormatter = DateTimeFormatter.ofPattern("d E", Locale.getDefault())

        val forecast = MockDataGenerator.dailyForecastEntity.dayItems.subList(0, 5).map { dayItem ->
            WidgetDailyForecastComparisonRemoteViewUiModel.DailyForecast("${dayItem.minTemperature.convertUnit(units.temperatureUnit)}/${
                dayItem.maxTemperature.convertUnit(units.temperatureUnit)
            }",
                dayItem.items.map { it.weatherCondition.value.dayWeatherIcon },
                dateFormatter.format(ZonedDateTime.parse(dayItem.dateTime.value)))
        }
        val mockModel = WidgetDailyForecastComparisonRemoteViewUiModel(WeatherProvider.enums.map {
            WidgetDailyForecastComparisonRemoteViewUiModel.Item(it, forecast)
        })
        return createContentView(mockModel, RemoteViewsMockGenerator.header, context)
    }
}