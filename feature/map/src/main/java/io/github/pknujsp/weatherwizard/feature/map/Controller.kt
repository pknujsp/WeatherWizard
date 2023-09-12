package io.github.pknujsp.weatherwizard.feature.map

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.views.MapView

class SimpleMapController {
    private var iMapController: IMapController? = null
    private val _time: MutableStateFlow<String> = MutableStateFlow("")
    val time: StateFlow<String> = _time

    fun init(mapView: MapView) {
        iMapController = mapView.controller
    }

    fun setTime(time: String) {
        _time.value = time
    }

    fun zoomIn() {
        iMapController?.zoomIn()
    }

    fun zoomOut() {
        iMapController?.zoomOut()
    }


}

interface RadarController {
    val playing: StateFlow<Boolean>
    val time: StateFlow<String>

    fun beforeRadar()
    fun nextRadar()

    fun play()
    fun currentRadar()
}

class RadarAdapter {
    private var radarController: RadarController? = null

    private val _time = MutableStateFlow("")
    val time: StateFlow<String> = _time

    private val _playing = MutableStateFlow(false)
    val playing: StateFlow<Boolean> = _playing

    fun setRadarController(scope: CoroutineScope, radarController: RadarController) {
        this.radarController = radarController
        scope.launch {
            launch {
                radarController.playing.collect {
                    _playing.value = it
                }
            }
            launch {
                radarController.time.collect {
                    _time.value = it
                }
            }
        }
    }

    fun beforeRadar() {
        radarController?.beforeRadar()
    }

    fun nextRadar() {
        radarController?.nextRadar()
    }

    fun play() {
        radarController?.play()
    }

    fun currentRadar() {
        radarController?.currentRadar()
    }
}