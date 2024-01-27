package io.github.pknujsp.everyweather.core.data.settings

import io.github.pknujsp.everyweather.core.model.settings.BasePreferenceModel
import io.github.pknujsp.everyweather.core.model.settings.PreferenceModel
import io.github.pknujsp.everyweather.core.model.settings.SettingsEntity
import kotlinx.coroutines.flow.SharedFlow

interface SettingsRepository {
    val settings: SharedFlow<SettingsEntity>

    suspend fun <V : PreferenceModel> update(type: BasePreferenceModel<V>, value: V)
}