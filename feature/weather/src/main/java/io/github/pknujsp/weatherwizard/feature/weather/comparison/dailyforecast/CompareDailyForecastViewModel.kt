package io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.domain.weather.compare.GetDailyForecastToCompareUseCase
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.CompareDailyForecast
import io.github.pknujsp.weatherwizard.feature.weather.comparison.common.CompareForecastViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompareDailyForecastViewModel @Inject constructor(
    private val getDailyForecastToCompareUseCase: GetDailyForecastToCompareUseCase,
    private val settingsRepository: SettingsRepository,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher
) : CompareForecastViewModel() {

    private val _dailyForecast = MutableStateFlow<UiState<CompareDailyForecastInfo>>(UiState.Loading)

    val dailyForecast: StateFlow<UiState<CompareDailyForecastInfo>> = _dailyForecast.asStateFlow()

    override fun load(args: RequestWeatherArguments) {
        viewModelScope.launch {
            args.run {
                withContext(ioDispatcher) {
                    val weatherDataRequest = WeatherDataRequest()
                    weatherProviders.forEach {
                        weatherDataRequest.addRequest(WeatherDataRequest.Coordinate(latitude, longitude),
                            setOf(MajorWeatherEntityType.DAILY_FORECAST),
                            it)
                    }

                    getDailyForecastToCompareUseCase(weatherDataRequest.finalRequests).map { entity ->
                        val (firstDate, endDate) = entity.run {
                            items.maxOf { ZonedDateTime.parse(it.second.dayItems.first().dateTime.value).toLocalDate() } to items.minOf {
                                ZonedDateTime.parse(it.second.dayItems.last().dateTime.value).toLocalDate()
                            }
                        }

                        val dates = firstDate.run {
                            var date = firstDate
                            val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("M/d\nE")
                            val list = mutableListOf<String>()
                            while (date <= endDate) {
                                list.add(date.format(dateFormatter))
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
                                    if (this == -1) entity.dayItems.size
                                    else this
                                }

                            provider to CompareDailyForecast(entity.dayItems.subList(firstIndex, endIndex), units)
                        }
                        UiState.Success(CompareDailyForecastInfo(items, dates))
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
    items: List<Pair<WeatherProvider, CompareDailyForecast>>, val dates: Array<String>
) {
    val weatherDataProviders = items.map { it.first }

    val items = (0..<items.first().second.items.size).map { i ->
        items.map { it.second.items[i] }
    }

    companion object {
        val itemWidth: Dp = 92.dp
    }
}