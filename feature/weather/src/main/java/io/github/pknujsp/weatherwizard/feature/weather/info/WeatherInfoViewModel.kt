package io.github.pknujsp.weatherwizard.feature.weather.info


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.WeatherInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WeatherInfoViewModel @Inject constructor() : ViewModel() {

    private val _weatherInfoEntity = MutableStateFlow<UiState<WeatherInfo>>(UiState.Loading)
    val weatherInfo = _weatherInfoEntity.asStateFlow()

}