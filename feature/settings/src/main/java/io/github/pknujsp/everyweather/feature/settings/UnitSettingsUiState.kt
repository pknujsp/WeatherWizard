package io.github.pknujsp.everyweather.feature.settings

import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedUnit


@Stable
interface UnitSettingsUiState : SettingsUiState {
    val temperatureUnit: TemperatureUnit
    val windSpeedUnit: WindSpeedUnit
    val precipitationUnit: PrecipitationUnit
}