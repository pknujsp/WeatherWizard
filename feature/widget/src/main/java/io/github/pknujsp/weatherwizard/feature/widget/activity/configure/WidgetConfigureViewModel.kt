package io.github.pknujsp.weatherwizard.feature.widget.activity.configure

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetEntity
import io.github.pknujsp.weatherwizard.feature.widget.activity.configure.model.WidgetModel
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetConfigureViewModel @Inject constructor(
    appSettingsRepository: SettingsRepository, savedStateHandle: SavedStateHandle, private val widgetRepository: WidgetRepository
) : ViewModel() {
    val units = appSettingsRepository.currentUnits
    val widget = WidgetModel(savedStateHandle.get<Int>("widgetId")!!,
        WidgetType.fromOrdinal(savedStateHandle.get<Int>("widgetType")!!),
        save = ::save)

    private val _action: MutableStateFlow<ConfigureActionState?> = MutableStateFlow(null)
    val action: StateFlow<ConfigureActionState?> = _action


    private fun save() {
        viewModelScope.launch {
            if (widget.locationType is LocationType.CustomLocation && widget.addressName.isEmpty()) {
                _action.value = ConfigureActionState.NO_LOCATION_IS_SELECTED
                return@launch
            }

            widgetRepository.add(WidgetEntity(id = widget.id,
                content = WidgetEntity.Content(
                    latitude = widget.latitude,
                    longitude = widget.longitude,
                    addressName = widget.addressName,
                    locationType = widget.locationType.key,
                    weatherProvider = widget.weatherProvider.key,
                ),
                widgetType = widget.widgetType))
            delay(50L)
            widget.onSaved = true
        }
    }
}