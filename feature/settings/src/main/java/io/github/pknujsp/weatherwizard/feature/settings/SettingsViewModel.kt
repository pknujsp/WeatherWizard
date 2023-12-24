package io.github.pknujsp.weatherwizard.feature.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.WidgetStarter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val widgetStarter: WidgetStarter,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
) : ViewModel() {

    private val _windSpeedUnit = MutableStateFlow<WindSpeedUnit>(WindSpeedUnit.default)
    val windSpeedUnit: StateFlow<WindSpeedUnit> = _windSpeedUnit

    private val _temperatureUnit = MutableStateFlow<TemperatureUnit>(TemperatureUnit.default)
    val temperatureUnit: StateFlow<TemperatureUnit> = _temperatureUnit

    private val _precipitationUnit = MutableStateFlow<PrecipitationUnit>(PrecipitationUnit.default)
    val precipitationUnit: StateFlow<PrecipitationUnit> = _precipitationUnit

    private val _weatherProvider = MutableStateFlow<WeatherProvider>(WeatherProvider.default)
    val weatherProvider: StateFlow<WeatherProvider> = _weatherProvider

    var widgetAutoRefreshInterval by mutableStateOf(RefreshInterval.default)
        private set

    init {
        viewModelScope.launch {
            _windSpeedUnit.value = settingsRepository.getWindSpeedUnit()
            _temperatureUnit.value = settingsRepository.getTemperatureUnit()
            _precipitationUnit.value = settingsRepository.getPrecipitationUnit()
            _weatherProvider.value = settingsRepository.getWeatherDataProvider()
        }
    }

    fun updateUnit(unit: WeatherDataUnit) {
        viewModelScope.launch {
            when (unit) {
                is WindSpeedUnit -> {
                    _windSpeedUnit.value = unit
                    settingsRepository.setWindSpeedUnit(unit)
                }

                is TemperatureUnit -> {
                    _temperatureUnit.value = unit
                    settingsRepository.setTemperatureUnit(unit)
                }

                is PrecipitationUnit -> {
                    _precipitationUnit.value = unit
                    settingsRepository.setPrecipitationUnit(unit)
                }
            }
        }
    }

    fun updateWeatherDataProvider(provider: WeatherProvider) {
        viewModelScope.launch {
            _weatherProvider.value = provider
            settingsRepository.setWeatherDataProvider(provider)
        }
    }

    fun refreshWidgets(context: Context) {
        viewModelScope.launch(ioDispatcher) {
            widgetStarter.start(context)
        }
    }

    fun updateWidgetAutoRefreshInterval(interval: RefreshInterval) {
        viewModelScope.launch {
            widgetAutoRefreshInterval = interval
            settingsRepository.setWidgetAutoRefreshInterval(interval)
        }
    }
}