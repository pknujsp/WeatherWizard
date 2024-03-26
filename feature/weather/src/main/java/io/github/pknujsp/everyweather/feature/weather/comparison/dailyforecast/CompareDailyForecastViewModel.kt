package io.github.pknujsp.everyweather.feature.weather.comparison.dailyforecast

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.everyweather.core.domain.weather.compare.GetDailyForecastToCompareUseCase
import io.github.pknujsp.everyweather.core.model.UiState
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.feature.weather.comparison.common.CompareForecastViewModel
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.CompareDailyForecast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompareDailyForecastViewModel @Inject constructor(
    private val getDailyForecastToCompareUseCase: GetDailyForecastToCompareUseCase,
    private val settingsRepository: SettingsRepository,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
) : CompareForecastViewModel() {
    private val _dailyForecast = MutableStateFlow<UiState<CompareDailyForecastInfo>>(UiState.Loading)
    val dailyForecast: StateFlow<UiState<CompareDailyForecastInfo>> = _dailyForecast.asStateFlow()

    override fun load(args: RequestWeatherArguments) {
        viewModelScope.launch {
            args.run {
                withContext(ioDispatcher) {
                    val weatherDataRequestBuilder = WeatherDataRequest.Builder()
                    weatherProviders.forEach {
                        weatherDataRequestBuilder.add(
                            WeatherDataRequest.Coordinate(targetLocation.latitude, targetLocation.longitude),
                            arrayOf(MajorWeatherEntityType.DAILY_FORECAST),
                            it,
                        )
                    }

                    getDailyForecastToCompareUseCase(weatherDataRequestBuilder.build()).map { entity ->
                        val (firstDate, endDate) = entity.run {
                            items.maxOf { ZonedDateTime.parse(it.second.dayItems.first().dateTime.value).toLocalDate() } to items.minOf {
                                ZonedDateTime.parse(it.second.dayItems.last().dateTime.value).toLocalDate()
                            }
                        }

                        val dates2 = mutableListOf<String>()
                        val dates = firstDate.run {
                            var date = firstDate
                            val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("M/d\nE")
                            val dateFormatter2 = java.time.format.DateTimeFormatter.ofPattern("M/d E")
                            val list = mutableListOf<String>()

                            while (date <= endDate) {
                                list.add(date.format(dateFormatter))
                                dates2.add(date.format(dateFormatter2))

                                date = date.plusDays(1)
                            }
                            list.toTypedArray()
                        }
                        val units = settingsRepository.settings.replayCache.last().units

                        val items = entity.items.map { (provider, entity) ->
                            val firstIndex =
                                entity.dayItems.indexOfFirst { ZonedDateTime.parse(it.dateTime.value).toLocalDate() == firstDate }
                            val endIndex =
                                entity.dayItems.indexOfFirst { ZonedDateTime.parse(it.dateTime.value).toLocalDate() > endDate }.run {
                                    if (this == -1) {
                                        entity.dayItems.size
                                    } else {
                                        this
                                    }
                                }

                            provider to CompareDailyForecast(entity.dayItems.subList(firstIndex, endIndex), units)
                        }
                        UiState.Success(CompareDailyForecastInfo(items, dates, dates2))
                    }
                }.onSuccess { compareDailyForecast ->
                    _dailyForecast.value = compareDailyForecast
                }.onFailure {
                    _dailyForecast.value = UiState.Error(it)
                }
            }
        }
    }
}

@Stable
class CompareDailyForecastInfo(
    items: List<Pair<WeatherProvider, CompareDailyForecast>>,
    val dates: Array<String>,
    dates2: List<String>,
) {
    val weatherDataProviders = items.map { it.first }

    val items = (0..<items.first().second.items.size).map { i ->
        items.map { it.second.items[i] }
    }

    val commons: Map<WeatherConditionCategory, String>

    init {
        val tempCommonDayItemMap = mutableMapOf<AmPm, MutableSet<WeatherConditionCategory>>()
        val finalCommonDayItemMap = mutableMapOf<WeatherConditionCategory, MutableList<DayItem>>()

        dates2.forEachIndexed { index, date ->
            items.forEach { (_, item) ->
                item.items[index].run {
                    for (i in weatherConditions.indices) {
                        val amPm = toAmPm(weatherConditions.size, i)
                        val category = weatherConditions[i]

                        if (amPm !in tempCommonDayItemMap) {
                            tempCommonDayItemMap[amPm] = mutableSetOf()
                        }
                        if (category !in tempCommonDayItemMap[amPm]!!) {
                            tempCommonDayItemMap[amPm]!!.add(category)
                        } else {
                            finalCommonDayItemMap.getOrPut(category) { mutableListOf() }.add(DayItem(date, amPm))
                        }
                    }
                }
            }
            tempCommonDayItemMap.clear()
        }

        commons = finalCommonDayItemMap.mapValues { (_, dayItems) ->
            StringBuilder().apply {
                dayItems.forEach {
                    appendLine("- ${it.date} ${amPmStringMap[it.amPm]}")
                }
            }.toString()
        }
    }

    companion object {
        val itemWidth: Dp = 92.dp

        private val amPmStringMap = LocalDateTime.of(2020, 1, 1, 9, 0).run {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("a")
            mapOf(AmPm.AM to format(formatter), AmPm.PM to withHour(15).format(formatter), AmPm.ALL to "")
        }

        private fun toAmPm(
            itemsCount: Int,
            index: Int,
        ): AmPm {
            return if (itemsCount == 2) {
                if (index == 0) {
                    AmPm.AM
                } else {
                    AmPm.PM
                }
            } else if (index == 0) {
                AmPm.ALL
            } else {
                AmPm.PM
            }
        }
    }
}

data class DayItem(
    val date: String,
    val amPm: AmPm,
)

enum class AmPm {
    AM, PM, ALL,
}