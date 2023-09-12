package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AirQualityViewModel @Inject constructor(
    private val airQualityRepository: AirQualityRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _airQuality: MutableStateFlow<UiState<AirQualityEntity>> = MutableStateFlow(UiState.Loading)
    val airQuality: StateFlow<UiState<AirQualityEntity>> = _airQuality

    fun loadAirQuality(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            airQualityRepository.getAirQuality(latitude, longitude).onSuccess {
                _airQuality.value = UiState.Success(it)
            }.onFailure {
                _airQuality.value = UiState.Error(it)
            }
        }
    }
}