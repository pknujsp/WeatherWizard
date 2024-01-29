package io.github.pknujsp.everyweather.feature.map.screen

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.rainviewer.RadarTilesRepository
import io.github.pknujsp.everyweather.core.model.UiState
import io.github.pknujsp.everyweather.core.model.onSuccess
import io.github.pknujsp.everyweather.feature.map.RadarController
import io.github.pknujsp.everyweather.feature.map.model.RadarTileEntity
import io.github.pknujsp.everyweather.feature.map.model.RadarUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RainViewerViewModel @Inject constructor(
    private val radarTilesRepository: RadarTilesRepository,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), RadarController {

    private val mutablePlaying = MutableStateFlow(false)
    override val playing: StateFlow<Boolean> = mutablePlaying.asStateFlow()

    private var playingJob: Job? = null

    private val mutableRadarUiState: MutableStateFlow<UiState<RadarUiState>> = MutableStateFlow(UiState.Loading)
    val radarUiState: StateFlow<UiState<RadarUiState>> = mutableRadarUiState.asStateFlow()

    private val mutableTimePosition = MutableStateFlow(-1)
    val timePosition: StateFlow<Int> = mutableTimePosition.asStateFlow()

    override val time: StateFlow<String> = flow {
        timePosition.collect { idx ->
            val radar = radarUiState.value
            val newTime = if (radar is UiState.Success) {
                radar.data.times[idx]
            } else {
                ""
            }
            emit(newTime)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    companion object {
        const val PLAY_DURATION = 1000L
        private const val PLAY_COUNT = 15
        private const val RADAR_TILE_FIRST_TIME = "RADAR_TILE_FIRST_TIME"
        private const val RADAR_TILE_DEFAULT_INDEX = "RADAR_TILE_DEFAULT_INDEX"
    }

    fun load(context: Context) {
        viewModelScope.launch {
            mutableRadarUiState.value = UiState.Loading

            withContext(dispatcher) {
                radarTilesRepository.getTiles().map {
                    val entities = it.radar.map { entity ->
                        RadarTileEntity(entity.path, entity.time.toLong())
                    }
                    val isRadarTileCached = isTileSavedInCache(it.radar.first().time, it.currentIndex)
                    if (!isRadarTileCached) {
                        saveRadarTileCache(it.radar.first().time, it.currentIndex)
                    }
                    RadarUiState(isRadarTileCached, entities, it.host, it.currentIndex, it.requestTime, context)
                }
            }.onSuccess {
                mutableRadarUiState.value = UiState.Success(it)
                mutableTimePosition.value = it.defaultIndex
            }.onFailure {
                mutableRadarUiState.value = UiState.Error(it)
            }
        }
    }

    private fun saveRadarTileCache(firstTime: Int, defaultIndex: Int) {
        savedStateHandle[RADAR_TILE_FIRST_TIME] = firstTime
        savedStateHandle[RADAR_TILE_DEFAULT_INDEX] = defaultIndex
    }

    private fun isTileSavedInCache(firstTime: Int, defaultIndex: Int): Boolean {
        val savedFirstTime = savedStateHandle.get<Int>(RADAR_TILE_FIRST_TIME)
        val savedDefaultIndex = savedStateHandle.get<Int>(RADAR_TILE_DEFAULT_INDEX)

        return if (savedDefaultIndex == null || savedFirstTime == null) {
            false
        } else {
            firstTime == savedFirstTime && defaultIndex == savedDefaultIndex
        }
    }

    private fun Int.calibrateTimePosition(max: Int): Int = when {
        this < 0 -> max
        this > max -> 0
        else -> this
    }

    override fun beforeRadar() {
        viewModelScope.launch {
            radarUiState.value.onSuccess {
                stop()
                mutableTimePosition.value = (timePosition.value - 1).calibrateTimePosition(it.times.lastIndex)
            }
        }
    }

    override fun nextRadar() {
        viewModelScope.launch {
            radarUiState.value.onSuccess {
                stop()
                mutableTimePosition.value = (timePosition.value + 1).calibrateTimePosition(it.times.lastIndex)
            }
        }
    }

    override fun play() {
        viewModelScope.launch {
            if (!stop()) {
                playingJob = launch(SupervisorJob()) {
                    mutablePlaying.value = true
                    radarUiState.value.onSuccess { radarUiState ->
                        repeat(PLAY_COUNT) {
                            mutableTimePosition.value = (timePosition.value + 1).calibrateTimePosition(radarUiState.times.lastIndex)
                            delay(PLAY_DURATION)
                        }
                    }
                    mutablePlaying.value = false
                    playingJob = null
                }
            }
        }
    }

    private fun stop(): Boolean = playingJob?.run {
        cancel()
        mutablePlaying.value = false
        playingJob = null
        true
    } ?: false

    override fun currentRadar() {
        viewModelScope.launch {
            stop()
            radarUiState.value.onSuccess {
                mutableTimePosition.value = it.defaultIndex
            }
        }
    }

}