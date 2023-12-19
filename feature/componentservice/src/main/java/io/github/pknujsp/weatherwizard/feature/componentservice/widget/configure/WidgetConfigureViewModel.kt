package io.github.pknujsp.weatherwizard.feature.componentservice.widget.configure

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.configure.model.WidgetModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetConfigureViewModel @Inject constructor(
    private val widgetRepository: WidgetRepository, appSettingsRepository: SettingsRepository,
) : ViewModel() {
    val units = appSettingsRepository.currentUnits.value
    val widget = WidgetModel(save = ::save)

    var action by mutableStateOf<ConfigureActionState?>(null)
        private set

    fun load(widgetId: Int, widgetType: WidgetType) {
        widget.apply {
            id = widgetId
            this.widgetType = widgetType
        }
    }

    private fun save() {
        viewModelScope.launch {
            action = null
            if (widget.location.locationType is LocationType.CustomLocation && widget.location.address.isEmpty()) {
                action = ConfigureActionState.NO_LOCATION_IS_SELECTED
                return@launch
            }

            widgetRepository.add(WidgetSettingsEntity(id = widget.id,
                location = widget.location,
                weatherProvider = widget.weatherProvider,
                widgetType = widget.widgetType))
            delay(50L)
            action = ConfigureActionState.SAVE_SUCCESS
        }
    }
}