package io.github.pknujsp.everyweather.feature.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    var isInitialized : Boolean? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            isInitialized = settingsRepository.isInitialized()
        }
    }
}