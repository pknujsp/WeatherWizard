package io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.model

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.ToCompareHourlyForecastEntity
import java.time.ZonedDateTime

@Stable
class HourlyForecastComparisonReport(
    items: List<Pair<WeatherProvider, List<ToCompareHourlyForecastEntity.Item>>>,
    times: List<Pair<Boolean, ZonedDateTime>>,
) : UiModel {

    val commonForecasts: Map<WeatherConditionCategory, String>

    init {
        items.run {
            val commons = mutableMapOf<Pair<Boolean, ZonedDateTime>, MutableSet<WeatherConditionCategory>>()
            items.forEach { item ->
                item.second.forEachIndexed() { i, forecast ->
                    commons.getOrPut(times[i]) { mutableSetOf() }.add(forecast.weatherCondition.value)
                }
            }

            val sameForecasts = commons.filter { it.value.size == 1 }.run {
                if (isEmpty()) {
                    emptyList()
                } else {
                    map { (time, categories) ->
                        time to categories.first()
                    }
                }
            }

            val categories = mutableMapOf<WeatherConditionCategory, MutableList<ZonedDateTime>>()
            sameForecasts.map { (time, category) ->
                categories.getOrPut(category) { mutableListOf() }.add(time.second)
            }

            val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("M.d E")
            commonForecasts = categories.mapValues { (_, times) ->
                times.parseDateTimeRanges(dateFormatter).toMarkdownList()
            }
        }
    }

    private fun List<Pair<String, List<String>>>.toMarkdownList(): String {
        return StringBuilder().apply {
            for ((day, times) in this@toMarkdownList) {
                appendLine("- **$day**")
                for (time in times) {
                    appendLine("  - $time")
                }
            }
        }.toString()
    }

    private fun List<ZonedDateTime>.parseDateTimeRanges(dateFormatter: java.time.format.DateTimeFormatter): List<Pair<String, List<String>>> {
        return groupBy { it.dayOfYear }.let { groups ->
            groups.map { (_, times) ->
                val date = times.first().format(dateFormatter)

                val ranges = times.let {
                    var lastTime = times.first().hour + 1

                    if (times.size == 1) {
                        listOf("${lastTime - 1}")
                    } else {
                        var newTime: Int
                        val hours = mutableListOf<MutableList<Int>>(mutableListOf())
                        var diff: Int
                        val lastIdx = times.size - 2

                        times.drop(1).forEachIndexed { i, dateTime ->
                            newTime = dateTime.hour + 1
                            diff = newTime - lastTime
                            hours.last().add(lastTime)

                            if (diff != 1) {
                                hours.add(mutableListOf())
                            }

                            if (i == lastIdx) {
                                hours.last().add(newTime)
                            }

                            lastTime = newTime
                        }

                        hours.map {
                            if (it.size == 1) {
                                "${it.first() - 1}"
                            } else {
                                "${it.first() - 1} - ${it.last() - 1}"
                            }
                        }
                    }
                }

                date to ranges
            }
        }
    }

    class Item(
        val times: String,
        val weatherConditionCategory: WeatherConditionCategory,
    )
}