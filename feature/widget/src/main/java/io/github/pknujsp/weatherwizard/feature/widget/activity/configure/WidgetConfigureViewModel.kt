package io.github.pknujsp.weatherwizard.feature.widget.activity.configure

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class WidgetConfigureViewModel @Inject constructor(
    appSettingsRepository: SettingsRepository
) : ViewModel() {
    val units = appSettingsRepository.currentUnits

}