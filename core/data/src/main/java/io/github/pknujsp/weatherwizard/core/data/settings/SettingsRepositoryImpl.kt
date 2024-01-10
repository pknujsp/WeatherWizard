package io.github.pknujsp.weatherwizard.core.data.settings

import android.util.Log
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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.channelFlow

class SettingsRepositoryImpl(
    private val appDataStore: AppDataStore
) : SettingsRepository, RepositoryInitializer {

    private val mutableSettings =
        MutableSharedFlow<SettingsEntity>(replay = 1, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val settings: SharedFlow<SettingsEntity> = mutableSettings.asSharedFlow()

    private companion object {
        val preferences = arrayOf(TemperatureUnit.Companion,
            WindSpeedUnit.Companion,
            PrecipitationUnit.Companion,
            WeatherProvider.Companion,
            RefreshInterval.Companion)
    }

    override suspend fun <V : PreferenceModel> update(type: BasePreferenceModel<V>, value: V) {
        appDataStore.save(type.key, value.key)
        init()
    }

    override suspend fun init() {
        load().run {
            mutableSettings.emit(SettingsEntity(units = CurrentUnits(temperatureUnit = this[TemperatureUnit]!! as TemperatureUnit,
                windSpeedUnit = this[WindSpeedUnit]!! as WindSpeedUnit,
                precipitationUnit = this[PrecipitationUnit]!! as PrecipitationUnit),
                weatherProvider = this[WeatherProvider]!! as WeatherProvider,
                widgetAutoRefreshInterval = this[RefreshInterval]!! as RefreshInterval).apply {
                Log.d("SettingsRepositoryImpl", "init: $this")
            })
        }
    }

    private suspend fun load(): Map<BasePreferenceModel<out PreferenceModel>, PreferenceModel> = preferences.associateWith { preference ->
        when (val value = appDataStore.readAsInt(preference.key)) {
            is DBEntityState.Exists -> preference.fromKey(value.data)
            is DBEntityState.NotExists -> preference.default
        }
    }


}