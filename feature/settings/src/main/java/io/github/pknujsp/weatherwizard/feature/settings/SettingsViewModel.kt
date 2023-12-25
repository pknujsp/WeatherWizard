package io.github.pknujsp.weatherwizard.feature.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.settings.BasePreferenceModel
import io.github.pknujsp.weatherwizard.core.model.settings.PreferenceModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.WidgetStarter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val widgetStarter: WidgetStarter,
) : ViewModel() {

    private val mutableMainSettingsUiState = MutableMainSettingsUiState(update = ::updatePreference)
    val mainSettingsUiState: MainSettingsUiState = mutableMainSettingsUiState

    init {
        viewModelScope.launch {
            settingsRepository.settings.value.let {
                mutableMainSettingsUiState.run {
                    weatherProvider = it.weatherProvider
                    widgetAutoRefreshInterval = it.widgetAutoRefreshInterval
                }
            }
        }
    }

    private fun updatePreference(type: BasePreferenceModel<out PreferenceModel>, value: PreferenceModel) {
        viewModelScope.launch {
            when (type) {
                RefreshInterval -> {
                    settingsRepository.update(RefreshInterval, value as RefreshInterval)
                }

                WeatherProvider -> {
                    settingsRepository.update(WeatherProvider, value as WeatherProvider)
                }
            }
        }
    }

    fun reDrawAppWidgets(context: Context) {
        viewModelScope.launch {
            widgetStarter.start(context)
        }
    }
}

private class MutableMainSettingsUiState(
    private val update: (BasePreferenceModel<out PreferenceModel>, PreferenceModel) -> Unit
) : MainSettingsUiState {

    override var weatherProvider: WeatherProvider by mutableStateOf(WeatherProvider.default)
    override var widgetAutoRefreshInterval: RefreshInterval by mutableStateOf(RefreshInterval.default)

    override fun <V : PreferenceModel> updatePreference(type: BasePreferenceModel<V>, value: V) {
        update(type, value)
        when (type) {
            RefreshInterval -> {
                widgetAutoRefreshInterval = value as RefreshInterval
            }

            WeatherProvider -> {
                weatherProvider = value as WeatherProvider
            }
        }
    }
}