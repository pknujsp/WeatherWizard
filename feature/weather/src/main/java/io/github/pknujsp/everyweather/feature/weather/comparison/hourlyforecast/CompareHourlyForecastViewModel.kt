package io.github.pknujsp.everyweather.feature.weather.comparison.hourlyforecast

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.common.util.toCalendar
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.everyweather.core.domain.weather.compare.GetHourlyForecastToCompareUseCase
import io.github.pknujsp.everyweather.core.model.UiState
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.ToCompareHourlyForecastEntity
import io.github.pknujsp.everyweather.core.ui.time.DateTimeInfo
import io.github.pknujsp.everyweather.core.ui.weather.item.DynamicDateTimeUiCreator
import io.github.pknujsp.everyweather.feature.weather.comparison.common.CompareForecastViewModel
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.CompareHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.HourlyForecastComparisonReport
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompareHourlyForecastViewModel
    @Inject
    constructor(
        private val getHourlyForecastToCompareUseCase: GetHourlyForecastToCompareUseCase,
        private val settingsRepository: SettingsRepository,
        @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
    ) : CompareForecastViewModel() {
        private val _hourlyForecast = MutableStateFlow<UiState<CompareHourlyForecastInfo>>(UiState.Loading)
        val hourlyForecast: StateFlow<UiState<CompareHourlyForecastInfo>> = _hourlyForecast.asStateFlow()

        private val _report = MutableStateFlow<UiState<HourlyForecastComparisonReport>>(UiState.Loading)
        val report: StateFlow<UiState<HourlyForecastComparisonReport>> = _report.asStateFlow()

        override fun load(args: RequestWeatherArguments) {
            viewModelScope.launch {
                args.run {
                    withContext(ioDispatcher) {
                        val weatherDataRequestBuilder = WeatherDataRequest.Builder()
                        weatherProviders.forEach {
                            weatherDataRequestBuilder.add(
                                WeatherDataRequest.Coordinate(targetLocation.latitude, targetLocation.longitude),
                                arrayOf(MajorWeatherEntityType.HOURLY_FORECAST),
                                it,
                            )
                        }
                        getHourlyForecastToCompareUseCase(weatherDataRequestBuilder.build()).map { entity ->
                            val (firstTime, endTime) =
                                entity.run {
                                    items.maxOf { ZonedDateTime.parse(it.second.first().dateTime.value) } to
                                        items.minOf {
                                            ZonedDateTime.parse(it.second.last().dateTime.value)
                                        }
                                }
                            val dayNightCalculator = DayNightCalculator(targetLocation.latitude, targetLocation.longitude)
                            val dayOrNightList = mutableListOf<Pair<Boolean, ZonedDateTime>>()
                            var time = firstTime
                            while (time <= endTime) {
                                dayOrNightList.add(calculateDayOrNight(dayNightCalculator, time) to time)
                                time = time.plusHours(1)
                            }

                            val units = settingsRepository.settings.replayCache.last().units
                            val entities = mutableListOf<Pair<WeatherProvider, List<ToCompareHourlyForecastEntity.Item>>>()

                            val items =
                                entity.items.map { (provider, items) ->
                                    val firstIndex = items.indexOfFirst { ZonedDateTime.parse(it.dateTime.value) == firstTime }
                                    val endIndex =
                                        items.indexOfFirst { ZonedDateTime.parse(it.dateTime.value) > endTime }.run {
                                            if (this == -1) {
                                                items.size
                                            } else {
                                                this
                                            }
                                        }

                                    provider to
                                        CompareHourlyForecast(
                                            items.subList(firstIndex, endIndex).apply {
                                                entities.add(provider to this)
                                            }.mapIndexed { i, item ->
                                                val dayOrNightPair = dayOrNightList[i]
                                                CompareHourlyForecast.Item(
                                                    id = i,
                                                    weatherCondition = item.weatherCondition,
                                                    hour = dayOrNightPair.second.hour.toString(),
                                                    temperature = item.temperature.convertUnit(units.temperatureUnit),
                                                    rainfallVolume = item.rainfallVolume.convertUnit(units.precipitationUnit),
                                                    snowfallVolume = item.snowfallVolume.convertUnit(units.precipitationUnit),
                                                    rainfallProbability = item.rainfallProbability,
                                                    snowfallProbability = item.snowfallProbability,
                                                    precipitationVolume = item.precipitationVolume.convertUnit(units.precipitationUnit),
                                                    precipitationProbability = item.precipitationProbability,
                                                    isDay = dayOrNightPair.first,
                                                )
                                            },
                                        )
                                }
                            val dateTimeInfo =
                                DynamicDateTimeUiCreator(
                                    dayOrNightList.map {
                                        it.second.toString()
                                    },
                                    CompareHourlyForecastInfo.itemWidth,
                                ).invoke()

                            UiState.Success(CompareHourlyForecastInfo(items, dateTimeInfo)) to
                                UiState.Success(
                                    HourlyForecastComparisonReport(
                                        entities,
                                        dayOrNightList,
                                    ),
                                )
                        }
                    }.onSuccess { (compareHourlyForecastInfo, hourlyForecastComparisonReport) ->
                        _hourlyForecast.value = compareHourlyForecastInfo
                        _report.value = hourlyForecastComparisonReport
                    }.onFailure {
                        _hourlyForecast.value = UiState.Error(it)
                    }
                }
            }
        }

        private fun calculateDayOrNight(
            dayNightCalculator: DayNightCalculator,
            dateTime: ZonedDateTime,
        ): Boolean = dayNightCalculator.calculate(dateTime.toCalendar()) == DayNightCalculator.DayNight.DAY
    }

@Stable
class CompareHourlyForecastInfo(
    items: List<Pair<WeatherProvider, CompareHourlyForecast>>,
    val dateTimeInfo: DateTimeInfo,
) {
    val weatherDataProviders = items.map { it.first }.toTypedArray()

    val items =
        items.run {
            val counts = items.first().second.items.size
            (0..<counts).map { i ->
                map { it.second.items[i] }.toTypedArray()
            }.toTypedArray()
        }

    companion object {
        val itemWidth: Dp = 54.dp
    }
}
