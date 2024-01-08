package io.github.pknujsp.weatherwizard.feature.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.settings.BasePreferenceModel
import io.github.pknujsp.weatherwizard.core.model.settings.PreferenceModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnitSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val mutableUnitSettingsUiState = MutableUnitSettingsUiState(::updatePreference)

    val unitSettingsUiState: UnitSettingsUiState = mutableUnitSettingsUiState

    init {
        viewModelScope.launch {
            settingsRepository.settings.replayCache.last().units.let {
                mutableUnitSettingsUiState.run {
                    temperatureUnit = it.temperatureUnit
                    windSpeedUnit = it.windSpeedUnit
                    precipitationUnit = it.precipitationUnit
                }
            }
        }
    }

    private fun updatePreference(type: BasePreferenceModel<out PreferenceModel>, value: PreferenceModel) {
        viewModelScope.launch {
            when (type) {
                TemperatureUnit -> {
                    settingsRepository.update(TemperatureUnit, value as TemperatureUnit)
                }

                WindSpeedUnit -> {
                    settingsRepository.update(WindSpeedUnit, value as WindSpeedUnit)
                }

                PrecipitationUnit -> {
                    settingsRepository.update(PrecipitationUnit, value as PrecipitationUnit)
                }
            }
        }
    }
}


private class MutableUnitSettingsUiState(
    private val update: (BasePreferenceModel<out PreferenceModel>, PreferenceModel) -> Unit
) : UnitSettingsUiState {

    override var temperatureUnit: TemperatureUnit by mutableStateOf(TemperatureUnit.default)
    override var windSpeedUnit: WindSpeedUnit by mutableStateOf(WindSpeedUnit.default)
    override var precipitationUnit: PrecipitationUnit by mutableStateOf(PrecipitationUnit.default)

    override fun <V : PreferenceModel> updatePreference(type: BasePreferenceModel<V>, value: V) {
        update(type, value)
        when (type) {
            TemperatureUnit -> {
                temperatureUnit = value as TemperatureUnit
            }

            WindSpeedUnit -> {
                windSpeedUnit = value as WindSpeedUnit
            }

            PrecipitationUnit -> {
                precipitationUnit = value as PrecipitationUnit
            }
        }

    }
}