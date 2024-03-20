package io.github.pknujsp.everyweather.feature.weather.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.everyweather.core.data.favorite.TargetLocationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WeatherMainViewModel
    @Inject
    constructor(
        targetLocationRepository: TargetLocationRepository,
    ) : ViewModel() {
        val selectedLocation: StateFlow<SelectedLocationModel?> =
            targetLocationRepository.targetLocation.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    }
