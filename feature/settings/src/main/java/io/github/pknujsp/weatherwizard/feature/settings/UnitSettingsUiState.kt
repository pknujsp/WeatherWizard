package io.github.pknujsp.weatherwizard.feature.settings

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit


@Stable
interface UnitSettingsUiState : SettingsUiState {
    val temperatureUnit: TemperatureUnit
    val windSpeedUnit: WindSpeedUnit
    val precipitationUnit: PrecipitationUnit
}