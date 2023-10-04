package io.github.pknujsp.weatherwizard.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _windSpeedUnit = MutableStateFlow<WindSpeedUnit>(WindSpeedUnit.default)
    val windSpeedUnit: StateFlow<WindSpeedUnit> = _windSpeedUnit

    private val _temperatureUnit = MutableStateFlow<TemperatureUnit>(TemperatureUnit.default)
    val temperatureUnit: StateFlow<TemperatureUnit> = _temperatureUnit

    private val _precipitationUnit = MutableStateFlow<PrecipitationUnit>(PrecipitationUnit.default)
    val precipitationUnit: StateFlow<PrecipitationUnit> = _precipitationUnit

    private val _weatherDataProvider = MutableStateFlow<WeatherDataProvider>(WeatherDataProvider.default)
    val weatherDataProvider: StateFlow<WeatherDataProvider> = _weatherDataProvider

    init {
        viewModelScope.launch {
            _windSpeedUnit.value = settingsRepository.getWindSpeedUnit()
            _temperatureUnit.value = settingsRepository.getTemperatureUnit()
            _precipitationUnit.value = settingsRepository.getPrecipitationUnit()
            _weatherDataProvider.value = settingsRepository.getWeatherDataProvider()
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

    fun updateWeatherDataProvider(provider: WeatherDataProvider) {
        viewModelScope.launch {
            _weatherDataProvider.value = provider
            settingsRepository.setWeatherDataProvider(provider)
        }
    }
}