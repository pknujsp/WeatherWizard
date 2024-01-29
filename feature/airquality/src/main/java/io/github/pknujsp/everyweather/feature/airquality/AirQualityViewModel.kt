package io.github.pknujsp.everyweather.feature.airquality

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.common.manager.FailedReason
import io.github.pknujsp.everyweather.core.data.aqicn.AirQualityRepository
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityEntity
import io.github.pknujsp.everyweather.core.model.airquality.SimpleAirQuality
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
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

    private val mutableAirQuality = MutableAirQualityUiState()
    val airQuality: AirQualityUiState = mutableAirQuality

    private var coordinate: Pair<Double, Double>? = null
    private var job: Job? = null

    fun reload() {
        viewModelScope.launch {
            mutableAirQuality.isLoading = true
            coordinate?.run {
                loadAirQuality(first, second)
            } ?: run {
                mutableAirQuality.isLoading = false
                mutableAirQuality.failedReason = FailedReason.UNKNOWN
            }
        }
    }

    fun loadAirQuality(latitude: Double, longitude: Double) {
        job?.cancel()
        job = viewModelScope.launch {
            coordinate = latitude to longitude
            mutableAirQuality.isLoading = true

            withContext(ioDispatcher) {
                airQualityRepository.getAirQuality(latitude, longitude)
            }.onSuccess {
                mutableAirQuality.entity = it
                mutableAirQuality.airQuality = createModel(it)
                mutableAirQuality.isLoading = false
            }.onFailure {
                mutableAirQuality.failedReason = FailedReason.UNKNOWN
                mutableAirQuality.isLoading = false
            }
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

private class MutableAirQualityUiState : AirQualityUiState {
    override var airQuality: SimpleAirQuality? by mutableStateOf(null)
    override var isLoading: Boolean by mutableStateOf(true)
    override var failedReason: StatefulFeature? by mutableStateOf(null)
    override var entity: AirQualityEntity? = null
}

@Stable
interface AirQualityUiState {
    val airQuality: SimpleAirQuality?
    val isLoading: Boolean
    val failedReason: StatefulFeature?
    val entity: AirQualityEntity?
}