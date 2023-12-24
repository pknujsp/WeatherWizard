package io.github.pknujsp.weatherwizard.core.data.settings

import io.github.pknujsp.weatherwizard.core.model.settings.BasePreferenceModel
import io.github.pknujsp.weatherwizard.core.model.settings.PreferenceModel
import io.github.pknujsp.weatherwizard.core.model.settings.SettingsEntity
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<SettingsEntity>

    suspend fun <V : PreferenceModel> update(
        preference: BasePreferenceModel<V>, value: V
    )
}