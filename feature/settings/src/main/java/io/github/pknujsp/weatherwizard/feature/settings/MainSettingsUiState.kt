package io.github.pknujsp.weatherwizard.feature.settings

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.settings.BasePreferenceModel
import io.github.pknujsp.weatherwizard.core.model.settings.PreferenceModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider


@Stable
interface MainSettingsUiState : SettingsUiState {
    val weatherProvider: WeatherProvider
    val widgetAutoRefreshInterval: RefreshInterval
}

interface SettingsUiState {
    fun <V : PreferenceModel> updatePreference(type: BasePreferenceModel<V>, value: V)
}