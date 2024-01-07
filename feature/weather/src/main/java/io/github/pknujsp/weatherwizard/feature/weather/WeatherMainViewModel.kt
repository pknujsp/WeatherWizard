package io.github.pknujsp.weatherwizard.feature.weather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherMainViewModel @Inject constructor(
    private val targetLocationRepository: TargetLocationRepository,
) : ViewModel() {

    var locationType: LocationType? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            locationType = targetLocationRepository.getCurrentTargetLocation().locationType
        }
    }
}