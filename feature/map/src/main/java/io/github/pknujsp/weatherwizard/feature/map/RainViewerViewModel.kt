package io.github.pknujsp.weatherwizard.feature.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RainViewerViewModel @Inject constructor(
    radarTilesRepository: RadarTilesRepository,
    @ApplicationContext context: Context,
) : ViewModel(), RadarController {
    private val playDelay: Long = 1000L
    private val playCounts = 20

    private val _playing = MutableStateFlow(false)
    override val playing: StateFlow<Boolean> = _playing

    private val _time: MutableStateFlow<String> = MutableStateFlow("")
    override val time: StateFlow<String> = _time

    private var playingJob: Job? = null

    private val _raderTiles: MutableStateFlow<UiState<RadarTilesOverlay>> = MutableStateFlow(UiState.Loading)
    val radarTiles: StateFlow<UiState<RadarTilesOverlay>> = _raderTiles

    private val _timePosition = MutableStateFlow(-1)
    val timePosition: StateFlow<Int> = _timePosition

    private val optionTileSize = 256 // can be 256 or 512.
    private val optionColorScheme = 8 // from 0 to 8. Check the https://rainviewer.com/api/color-schemes.html for additional information
    private val optionSmoothData = 0 // 0 - not smooth, 1 - smooth
    private val optionSnowColors = 1 // 0 - do not show snow colors, 1 - show snow colors

    val minZoomLevel: Float = 2f
    val maxZoomLevel: Float = 19f

    init {
        viewModelScope.launch(Dispatchers.IO) {
            radarTilesRepository.getTiles().onSuccess {
                val radarTilesOverlay = RadarTilesOverlay(context = context,
                    radarTiles = it,
                    minZoomLevel = minZoomLevel,
                    maxZoomLevel = maxZoomLevel,
                    optionTileSize = optionTileSize,
                    optionColorScheme = optionColorScheme,
                    optionSmoothData = optionSmoothData,
                    optionSnowColors = optionSnowColors,
                    requestTime = it.requestTime)
                _raderTiles.value = UiState.Success(radarTilesOverlay)
                _timePosition.emit(it.currentIndex)
                _time.value = radarTilesOverlay.times[it.currentIndex]
            }.onFailure {
                _raderTiles.value = UiState.Error(it)
            }
        }
    }

    private fun Int.calibrateTimePosition(max: Int): Int = when {
        this < 0 -> max
        this > max -> 0
        else -> this
    }


    override fun beforeRadar() {
        viewModelScope.launch {
            radarTiles.value.onSuccess {
                stop()
                _timePosition.emit((timePosition.value - 1).calibrateTimePosition(it.overlays.lastIndex))
                _time.value = it.times[timePosition.value]
            }
        }
    }

    override fun nextRadar() {
        viewModelScope.launch {
            radarTiles.value.onSuccess {
                stop()
                _timePosition.emit((timePosition.value + 1).calibrateTimePosition(it.overlays.lastIndex))
                _time.value = it.times[timePosition.value]
            }
        }
    }

    override fun play() {
        viewModelScope.launch {
            if (!stop()) {
                playingJob = launch {
                    _playing.value = true
                    radarTiles.value.onSuccess { radarTiles ->
                        repeat(playCounts) {
                            _timePosition.emit((timePosition.value + 1).calibrateTimePosition(radarTiles.overlays.lastIndex))
                            _time.value = radarTiles.times[timePosition.value]
                            delay(playDelay)
                        }
                    }
                    _playing.value = false
                    playingJob = null
                }
            }
        }
    }

    private fun stop(): Boolean = playingJob?.run {
        cancel()
        _playing.value = false
        playingJob = null
        true
    } ?: false

    override fun currentRadar() {
        viewModelScope.launch {
            stop()
            _timePosition.emit((radarTiles.value as UiState.Success).data.currentIndex)
        }
    }
}