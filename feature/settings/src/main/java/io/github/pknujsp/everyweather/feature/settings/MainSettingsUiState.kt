package io.github.pknujsp.everyweather.feature.settings

import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.everyweather.core.model.settings.BasePreferenceModel
import io.github.pknujsp.everyweather.core.model.settings.PreferenceModel
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider


@Stable
interface MainSettingsUiState : SettingsUiState {
    val weatherProvider: WeatherProvider
    val widgetAutoRefreshInterval: RefreshInterval
}

interface SettingsUiState {
    fun <V : PreferenceModel> updatePreference(type: BasePreferenceModel<V>, value: V)
}