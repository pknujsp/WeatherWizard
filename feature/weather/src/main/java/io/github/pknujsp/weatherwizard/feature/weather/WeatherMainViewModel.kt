package io.github.pknujsp.weatherwizard.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetAreaRepository
import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherMainViewModel @Inject constructor(
    private val targetAreaRepository: TargetAreaRepository,
) : ViewModel() {

    private val _targetAreaType = MutableStateFlow<TargetAreaType?>(null)
    val targetAreaType : StateFlow<TargetAreaType?> = _targetAreaType

    init {
        viewModelScope.launch {
            _targetAreaType.value = targetAreaRepository.getTargetArea()
        }
    }
}