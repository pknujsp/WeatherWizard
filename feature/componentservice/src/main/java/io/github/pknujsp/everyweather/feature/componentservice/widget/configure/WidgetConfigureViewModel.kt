package io.github.pknujsp.everyweather.feature.componentservice.widget.configure

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.data.widget.WidgetRepository
import io.github.pknujsp.everyweather.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.feature.componentservice.widget.configure.model.WidgetModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WidgetConfigureViewModel @Inject constructor(
    private val widgetRepository: WidgetRepository,
    private val appSettingsRepository: SettingsRepository,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val units get() = appSettingsRepository.settings.replayCache.last().units
    val widget = savedStateHandle.run {
        WidgetModel(get<Int>("widgetId")!!, get<Int>("widgetType")!!, ::save)
    }

    var action by mutableStateOf<ConfigureActionState?>(null)
        private set

    private fun save() {
        viewModelScope.launch {
            action = null

            if (widget.location.locationType is LocationType.CustomLocation && widget.location.address.isEmpty()) {
                action = ConfigureActionState.NO_LOCATION_IS_SELECTED
                return@launch
            }

            withContext(ioDispatcher) {
                widgetRepository.add(WidgetSettingsEntity(id = widget.widgetId,
                    location = widget.location,
                    weatherProviders = if (widget.displayAllWeatherProviders) {
                        WeatherProvider.enums.toList()
                    } else {
                        listOf(widget.weatherProvider)
                    },
                    widgetType = widget.widgetType))
                delay(50L)
            }
            action = ConfigureActionState.SAVE_SUCCESS
        }
    }
}