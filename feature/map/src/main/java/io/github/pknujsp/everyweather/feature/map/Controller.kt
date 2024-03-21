package io.github.pknujsp.everyweather.feature.map

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.api.IMapController
import org.osmdroid.views.MapView

@Stable
class SimpleMapController {
    private var iMapController: IMapController? = null
    private val _time: MutableStateFlow<String> = MutableStateFlow("")
    val time: StateFlow<String> = _time.asStateFlow()

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

@Stable
interface RadarController {
    val playing: StateFlow<Boolean>
    val time: StateFlow<String>

    fun beforeRadar()

    fun nextRadar()

    fun play()

    fun currentRadar()
}
