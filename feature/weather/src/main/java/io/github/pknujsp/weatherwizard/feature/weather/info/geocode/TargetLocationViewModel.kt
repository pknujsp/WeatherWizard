package io.github.pknujsp.weatherwizard.feature.weather.info.geocode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TargetLocationViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private var job: Job? = null

    private val mutableTopAppBarUiState = MutableTopAppBarUiState()
    val topAppBarUiState: TopAppBarUiState = mutableTopAppBarUiState

    fun setLocation(location: TargetLocationModel) {
        job?.cancel()
        job = viewModelScope.launch {
            val result = if (location.address == null || location.country == null) {
                getCurrentLocationAddress(location)
            } else {
                LocationInfo(location.address, location.country)
            }

            result?.run {
                mutableTopAppBarUiState.address = address
                mutableTopAppBarUiState.country = country
            }
        }
    }

    private suspend fun getCurrentLocationAddress(location: TargetLocationModel): LocationInfo? {
        return withContext(dispatcher) {
            val cache = getCurrentLocationUseCase.currentLocationFlow.replayCache.lastOrNull { it.time > location.time }
            if (cache != null) {
                return@withContext if (cache is CurrentLocationResultState.SuccessWithAddress) {
                    LocationInfo(cache.address, cache.country)
                } else {
                    null
                }
            }

            getCurrentLocationUseCase.currentLocationFlow.filter {
                it.time >= location.time
            }.map {
                if (it is CurrentLocationResultState.SuccessWithAddress) {
                    LocationInfo(it.address, it.country)
                } else {
                    null
                }
            }.lastOrNull()
        }
    }

    fun setPrimaryArguments(weatherProvider: WeatherProvider, dateTime: String) {
        viewModelScope.launch {
            mutableTopAppBarUiState.weatherProvider = weatherProvider
            mutableTopAppBarUiState.dateTime = dateTime
        }
    }

}

private class MutableTopAppBarUiState : TopAppBarUiState {
    override var dateTime by mutableStateOf("")
    override var weatherProvider by mutableStateOf(WeatherProvider.default)
    override var address: String? by mutableStateOf(null)
    override var country: String? by mutableStateOf(null)
}

private data class LocationInfo(
    val address: String?,
    val country: String?,
)