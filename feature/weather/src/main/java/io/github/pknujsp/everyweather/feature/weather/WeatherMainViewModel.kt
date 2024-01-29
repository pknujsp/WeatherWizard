package io.github.pknujsp.everyweather.feature.weather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherMainViewModel @Inject constructor(
    private val targetLocationRepository: TargetLocationRepository,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    var locationType: LocationType? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            targetLocationRepository.targetLocation.filterNotNull().distinctUntilChanged().collectLatest {
                locationType = it.locationType
            }
        }
    }
}