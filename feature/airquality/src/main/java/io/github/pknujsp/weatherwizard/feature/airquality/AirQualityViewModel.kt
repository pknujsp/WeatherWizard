package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity
import io.github.pknujsp.weatherwizard.core.model.airquality.SimpleAirQuality
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AirQualityViewModel @Inject constructor(
    private val airQualityRepository: AirQualityRepository,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _airQuality: MutableStateFlow<UiState<SimpleAirQuality>> = MutableStateFlow(UiState.Loading)
    val airQuality: StateFlow<UiState<SimpleAirQuality>> = _airQuality.asStateFlow()

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    fun reload() {
        viewModelScope.launch {
            _airQuality.value = UiState.Loading
            loadAirQuality(latitude, longitude)
        }
    }

    fun loadAirQuality(latitude: Double, longitude: Double) {
        viewModelScope.launch(ioDispatcher) {
            this@AirQualityViewModel.latitude = latitude
            this@AirQualityViewModel.longitude = longitude

            _airQuality.value = airQualityRepository.getAirQuality(latitude, longitude).fold(onSuccess = { airQuality ->
                UiState.Success(createModel(airQuality))
            }, onFailure = { throwable ->
                UiState.Error(throwable)
            })
        }
    }

    private fun createModel(airQuality: AirQualityEntity): SimpleAirQuality = airQuality.let {
        val dataMeasurementTime = ZonedDateTime.parse(it.info.dataMeasurementTime)
        val info = SimpleAirQuality.Info(dataMeasurementTime = dataMeasurementTime.format(DateTimeFormatter.ofPattern("M/d E HH:mm")),
            stationName = it.info.stationName)
        val current = SimpleAirQuality.Current(it.current.aqi,
            it.current.co,
            it.current.no2,
            it.current.o3,
            it.current.pm10,
            it.current.pm25,
            it.current.so2)
        val dailyForecast = createDailyForecast(it)

        SimpleAirQuality(current, info, dailyForecast)
    }


    private fun createDailyForecast(airQualityEntity: AirQualityEntity) = airQualityEntity.dailyForecast.items.run {
        val indexes = map { item ->
            item to item.getAqi().valueNotNull()
        }
        val maxIndex = indexes.maxOf { it.second.value }
        val minIndex = indexes.minOf { it.second.value }
        val indexRange = (maxIndex - minIndex).toFloat()

        indexes.map {
            SimpleAirQuality.DailyItem(dateTime = LocalDate.parse(it.first.date),
                aqi = it.second,
                barHeightRatio = ((it.second.value - minIndex) / indexRange).coerceAtLeast(0.2f).coerceAtMost(1f))
        }
    }

}