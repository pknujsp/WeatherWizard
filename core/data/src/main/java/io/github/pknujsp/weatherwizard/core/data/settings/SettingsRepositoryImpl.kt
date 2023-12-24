package io.github.pknujsp.weatherwizard.core.data.settings

import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializer
import io.github.pknujsp.weatherwizard.core.database.AppDataStore
import io.github.pknujsp.weatherwizard.core.model.DBEntityState
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.settings.BasePreferenceModel
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.settings.PreferenceModel
import io.github.pknujsp.weatherwizard.core.model.settings.SettingsEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val appDataStore: AppDataStore
) : SettingsRepository, RepositoryInitializer {
    private val mutableSettings = MutableStateFlow(SettingsEntity())
    override val settings: StateFlow<SettingsEntity> = mutableSettings.asStateFlow()

    override suspend fun <V : PreferenceModel> update(preference: BasePreferenceModel<V>, value: V) {
        appDataStore.save(preference.key, value.key)
    }

    override suspend fun init() {
        load().run {
            mutableSettings.value = SettingsEntity(units = CurrentUnits(temperatureUnit = this[TemperatureUnit]!! as TemperatureUnit,
                windSpeedUnit = this[WindSpeedUnit]!! as WindSpeedUnit,
                precipitationUnit = this[PrecipitationUnit]!! as PrecipitationUnit),
                weatherProvider = this[WeatherProvider]!! as WeatherProvider,
                widgetAutoRefreshInterval = this[RefreshInterval]!! as RefreshInterval)
        }
    }

    private suspend fun load(): Map<BasePreferenceModel<out PreferenceModel>, PreferenceModel> = arrayOf(TemperatureUnit.Companion,
        WindSpeedUnit.Companion,
        PrecipitationUnit.Companion,
        WeatherProvider.Companion,
        RefreshInterval.Companion).associateWith { preference ->
        when (val value = appDataStore.readAsInt(preference.key)) {
            is DBEntityState.Exists -> preference.fromKey(value.data)
            is DBEntityState.NotExists -> preference.default
        }
    }


}