package io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.compare.GetDailyForecastToCompareUseCase
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.CompareDailyForecast
import io.github.pknujsp.weatherwizard.feature.weather.comparison.common.CompareForecastViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompareDailyForecastViewModel @Inject constructor(
    private val getDailyForecastToCompareUseCase: GetDailyForecastToCompareUseCase, private val settingsRepository: SettingsRepository
) : CompareForecastViewModel() {

    private val _dailyForecast =
        MutableStateFlow<UiState<io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast.CompareDailyForecastInfo>>(UiState
            .Loading)

    val dailyForecast: StateFlow<UiState<io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast.CompareDailyForecastInfo>> =
        _dailyForecast

    override fun load(args: RequestWeatherDataArgs) {
        viewModelScope.launch(Dispatchers.IO) {
            args.run {
                val requestId = System.currentTimeMillis()
                getDailyForecastToCompareUseCase(latitude, longitude, weatherDataProviders, requestId).onSuccess { entity ->
                    val (firstDate, endDate) = entity.run {
                        items.maxOf { ZonedDateTime.parse(it.second.items.first().dateTime.value).toLocalDate() } to items.minOf {
                            ZonedDateTime.parse(it.second.items.last().dateTime.value).toLocalDate()
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
                    val units = settingsRepository.currentUnits.value

                    val items = entity.items.map { (provider, entity) ->
                        val firstIndex = entity.items.indexOfFirst { ZonedDateTime.parse(it.dateTime.value).toLocalDate() == firstDate }
                        val endIndex = entity.items.indexOfFirst { ZonedDateTime.parse(it.dateTime.value).toLocalDate() > endDate }.run {
                            if (this == -1) entity.items.size
                            else this
                        }

                        provider to CompareDailyForecast(entity.items.subList(firstIndex, endIndex), units)
                    }
                    _dailyForecast.value = UiState.Success(CompareDailyForecastInfo(items, dates))
                }.onFailure {
                    _dailyForecast.value = UiState.Error(it)
                }
            }
        }
    }

}

class CompareDailyForecastInfo(
    items: List<Pair<WeatherDataProvider, CompareDailyForecast>>, val dates: Array<String>
) {
    val weatherDataProviders = items.map { it.first }.toTypedArray()
    val items =
        (0..<items.first().second.items.size).map { i ->
            items.map { it.second.items[i] }.toTypedArray()
        }.toTypedArray()


    companion object {
        val itemWidth: Dp = 92.dp
    }
}