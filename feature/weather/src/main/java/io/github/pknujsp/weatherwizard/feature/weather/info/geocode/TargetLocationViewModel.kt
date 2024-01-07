package io.github.pknujsp.weatherwizard.feature.weather.info.geocode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TargetLocationViewModel @Inject constructor(
    private val nominatimRepository: NominatimRepository, @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private var job: Job? = null

    private val mutableTopAppBarUiState = MutableTopAppBarUiState()
    var topAppBarUiState: TopAppBarUiState = mutableTopAppBarUiState

    fun setLocation(location: TargetLocationModel) {
        job?.cancel()
        job = viewModelScope.launch {
            val result = if (location.address == null || location.country == null) {
                reverseGeoCode(location)
            } else {
                LocationInfo(location.address, location.country)
            }

            result?.run {
                mutableTopAppBarUiState.address = address
                mutableTopAppBarUiState.country = country
            }
        }
    }

    private suspend fun reverseGeoCode(location: TargetLocationModel): LocationInfo? {
        return withContext(dispatcher) {
            nominatimRepository.reverseGeoCode(location.latitude, location.longitude).map {
                LocationInfo(it.simpleDisplayName, it.country)
            }
        }.getOrNull()
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