package io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.compare.GetHourlyForecastToCompareUseCase
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.CompareHourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastComparisonReport
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.SimpleHourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.ToCompareHourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.ui.weather.item.DynamicDateTimeUiCreator
import io.github.pknujsp.weatherwizard.feature.weather.comparison.common.CompareForecastViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompareHourlyForecastViewModel @Inject constructor(
    private val getHourlyForecastToCompareUseCase: GetHourlyForecastToCompareUseCase,
    private val settingsRepository: SettingsRepository
) : CompareForecastViewModel() {

    private val _hourlyForecast = MutableStateFlow<UiState<CompareHourlyForecastInfo>>(UiState.Loading)
    val hourlyForecast: StateFlow<UiState<CompareHourlyForecastInfo>> = _hourlyForecast

    private val _report = MutableStateFlow<UiState<HourlyForecastComparisonReport>>(UiState.Loading)
    val report: StateFlow<UiState<HourlyForecastComparisonReport>> = _report

    override fun load(args: RequestWeatherDataArgs) {
        viewModelScope.launch(Dispatchers.IO) {
            args.run {
                val requestId = System.currentTimeMillis()
                getHourlyForecastToCompareUseCase(latitude, longitude, weatherProviders, requestId).onSuccess { entity ->
                    val (firstTime, endTime) = entity.run {
                        items.maxOf { ZonedDateTime.parse(it.second.first().dateTime.value) } to items.minOf {
                            ZonedDateTime.parse(it.second
                                .last().dateTime.value)
                        }
                    }
                    val dayNightCalculator = DayNightCalculator(latitude, longitude)
                    val dayOrNightList = mutableListOf<Pair<Boolean, ZonedDateTime>>()
                    var time = firstTime
                    while (time <= endTime) {
                        dayOrNightList.add(
                            calculateDayOrNight(dayNightCalculator, time) to time
                        )
                        time = time.plusHours(1)
                    }

                    val units = settingsRepository.currentUnits.value
                    val entities = mutableListOf<Pair<WeatherProvider, List<ToCompareHourlyForecastEntity.Item>>>()

                    val items = entity.items.map { (provider, items) ->
                        val firstIndex = items.indexOfFirst { ZonedDateTime.parse(it.dateTime.value) == firstTime }
                        val endIndex = items.indexOfFirst { ZonedDateTime.parse(it.dateTime.value) > endTime }.run {
                            if (this == -1) items.size
                            else this
                        }

                        provider to CompareHourlyForecast(items.subList(firstIndex, endIndex).apply {
                            entities.add(provider to this)
                        }.mapIndexed { i, item ->
                            val dayOrNightPair = dayOrNightList[i]
                            CompareHourlyForecast.Item(id = i,
                                weatherCondition = item.weatherCondition,
                                hour = dayOrNightPair.second.hour.toString(),
                                temperature = item.temperature.convertUnit(units.temperatureUnit),
                                rainfallVolume = item.rainfallVolume.convertUnit(units.precipitationUnit),
                                snowfallVolume = item.snowfallVolume.convertUnit(units.precipitationUnit),
                                rainfallProbability = item.rainfallProbability,
                                snowfallProbability = item.snowfallProbability,
                                precipitationVolume = item.precipitationVolume.convertUnit(units.precipitationUnit),
                                precipitationProbability = item.precipitationProbability,
                                isDay = dayOrNightPair.first)
                        })
                    }
                    val dateTimeInfo = DynamicDateTimeUiCreator(dayOrNightList.map {
                        it.second.toString()
                    }, CompareHourlyForecastInfo.itemWidth).invoke()

                    _hourlyForecast.value = UiState.Success(CompareHourlyForecastInfo(items, dateTimeInfo))
                    analysis(dayOrNightList, entities)
                }.onFailure {
                    _hourlyForecast.value = UiState.Error(it)
                }
            }
        }
    }

    private fun calculateDayOrNight(dayNightCalculator: DayNightCalculator, dateTime: ZonedDateTime): Boolean =
        dayNightCalculator.calculate(dateTime.toCalendar()) == DayNightCalculator.DayNight.DAY

    private fun analysis(
        times: List<Pair<Boolean, ZonedDateTime>>, entities: List<Pair<WeatherProvider,
                List<ToCompareHourlyForecastEntity.Item>>>
    ) {
        _report.value = UiState.Success(HourlyForecastComparisonReport(entities, times))
    }
}

class CompareHourlyForecastInfo(
    items: List<Pair<WeatherProvider, CompareHourlyForecast>>, val dateTimeInfo: SimpleHourlyForecast.DateTimeInfo
) {
    val weatherDataProviders = items.map { it.first }.toTypedArray()
    val items = items.run {
        val counts = items.first().second.items.size
        (0..<counts).map { i ->
            map { it.second.items[i] }.toTypedArray()
        }.toTypedArray()
    }

    companion object {
        val itemWidth: Dp = 54.dp
    }
}