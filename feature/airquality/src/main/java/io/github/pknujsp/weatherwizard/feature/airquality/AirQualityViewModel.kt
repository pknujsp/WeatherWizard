package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity
import io.github.pknujsp.weatherwizard.core.model.airquality.SimpleAirQuality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AirQualityViewModel @Inject constructor(
    private val airQualityRepository: AirQualityRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _airQuality: MutableStateFlow<UiState<SimpleAirQuality>> = MutableStateFlow(UiState.Loading)
    val airQuality: StateFlow<UiState<SimpleAirQuality>> = _airQuality

    fun loadAirQuality(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            airQualityRepository.getAirQuality(latitude, longitude).onSuccess {
                _airQuality.value = UiState.Success(createModel(it))
            }.onFailure {
                _airQuality.value = UiState.Error(it)
            }
        }
    }

    private fun createModel(airQuality: AirQualityEntity): SimpleAirQuality =
        airQuality.let {
            val info =
                SimpleAirQuality.Info(dataMeasurementTime = it.info.dataMeasurementTime.format(DateTimeFormatter.ofPattern("M/d E HH:mm")),
                    stationName =
                    it.info.stationName)
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
            item to item.aqi.valueNotNull()
        }
        val maxIndex = indexes.maxOf { it.second.value }
        val minIndex = indexes.minOf { it.second.value }
        val indexRange = (maxIndex - minIndex).toFloat()

        indexes.map {
            SimpleAirQuality.DailyItem(
                dateTime = it.first.date,
                aqi = it.second,
                barHeightRatio = ((it.second.value - minIndex) / indexRange).coerceAtLeast(0.2f).coerceAtMost(1f)
            )
        }
    }

}