package io.github.pknujsp.weatherwizard.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetAreaRepository
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherMainViewModel @Inject constructor(
    private val targetAreaRepository: TargetAreaRepository,
) : ViewModel() {

    private val _locationType = MutableStateFlow<LocationType?>(null)
    val locationType : StateFlow<LocationType?> = _locationType

    init {
        viewModelScope.launch {
            _locationType.value = targetAreaRepository.getTargetArea()
        }
    }
}