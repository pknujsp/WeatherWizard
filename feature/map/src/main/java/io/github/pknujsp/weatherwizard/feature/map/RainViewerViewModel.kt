package io.github.pknujsp.weatherwizard.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.feature.map.model.RadarTileEntity
import io.github.pknujsp.weatherwizard.feature.map.model.RadarUiState
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
import kotlin.text.Typography.times

@HiltViewModel
class RainViewerViewModel @Inject constructor(
    private val radarTilesRepository: RadarTilesRepository, @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher
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
        private const val playDelay = 1000L
        private const val playCounts = 15
    }

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            mutableRadarUiState.value = UiState.Loading

            withContext(dispatcher) { radarTilesRepository.getTiles() }.onSuccess {
                val entities = it.radar.map { entity ->
                    RadarTileEntity(entity.path, entity.time.toLong())
                }
                val radarUiState = RadarUiState(entities, it.host, it.currentIndex, it.requestTime)

                mutableRadarUiState.value = UiState.Success(radarUiState)
                mutableTimePosition.value = it.currentIndex
            }.onFailure {
                mutableRadarUiState.value = UiState.Error(it)
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
                    radarUiState.value.onSuccess {  radarUiState ->
                        repeat(playCounts) {
                            mutableTimePosition.value = (timePosition.value + 1).calibrateTimePosition(radarUiState.times.lastIndex)
                            delay(playDelay)
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

    override fun onCleared() {
        super.onCleared()
    }
}