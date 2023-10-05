package io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import java.time.ZonedDateTime

class HourlyForecastComparisonReport(
    items: List<Pair<WeatherDataProvider, List<ToCompareHourlyForecastEntity.Item>>>,
    times: List<Pair<Boolean, ZonedDateTime>>,
) : UiModel {

    val commonForecasts: Map<WeatherConditionCategory, Item>

    init {
        items.run {
            val commons = mutableMapOf<Pair<Boolean, ZonedDateTime>, MutableSet<WeatherConditionCategory>>()
            items.forEach { item->
                item.second.forEachIndexed() { i, forecast ->
                    commons.getOrPut(times[i]) { mutableSetOf() }.add(forecast.weatherCondition.value)
                }
            }

            val commonsMap = commons.filter { it.value.size == 1 }.run {
                if (isEmpty()) {
                    emptyArray()
                } else {
                    map { (time, categories) ->
                        time to categories.first()
                    }.toTypedArray()
                }
            }
            val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("M.d E HH:mm")
            val categories = mutableMapOf<WeatherConditionCategory, MutableList<Pair<String, Boolean>>>()
            commonsMap.map { (time, category) ->
                categories.getOrPut(category) { mutableListOf() }.add(time.second.format(timeFormatter) to time.first)
            }

            commonForecasts = categories.map { (category, times) ->
                category to Item(times, category)
            }.toMap()
        }
    }

    class Item(
        times: List<Pair<String, Boolean>>,
        val weatherConditionCategory: WeatherConditionCategory,
    ) {
        val timesWithIcon = times.map {
            it.first to weatherConditionCategory.getWeatherIconByTimeOfDay(it.second)
        }
    }
}