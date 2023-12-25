package io.github.pknujsp.weatherwizard.core.model.settings

import io.github.pknujsp.weatherwizard.core.model.Model
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

data class SettingsEntity(
    val units: CurrentUnits = CurrentUnits(),
    val weatherProvider: WeatherProvider = WeatherProvider.default,
    val widgetAutoRefreshInterval: RefreshInterval = RefreshInterval.default,
) : Model