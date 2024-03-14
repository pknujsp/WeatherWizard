package io.github.pknujsp.everyweather.feature.weather.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    fun replaceWeatherProvider(weatherProvider: WeatherProvider) {
        viewModelScope.launch {
            settingsRepository.update(WeatherProvider, weatherProvider)
        }
    }
}