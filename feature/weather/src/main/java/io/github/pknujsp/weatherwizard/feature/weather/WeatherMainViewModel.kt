package io.github.pknujsp.weatherwizard.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherMainViewModel @Inject constructor(
    private val targetLocationRepository: TargetLocationRepository,
) : ViewModel() {

    private val _locationType = MutableStateFlow<LocationType?>(null)
    val locationType : StateFlow<LocationType?> = _locationType

    init {
        viewModelScope.launch {
            _locationType.value = targetLocationRepository.getTargetLocation().locationType
        }
    }
}