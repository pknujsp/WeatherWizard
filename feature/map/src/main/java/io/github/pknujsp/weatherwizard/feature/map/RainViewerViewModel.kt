package io.github.pknujsp.weatherwizard.feature.map

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.rainviewer.RadarTiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonElement
import org.osmdroid.views.overlay.TilesOverlay
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RainViewerViewModel @Inject constructor() : ViewModel() {
    private val _raderTiles: MutableStateFlow<UiState<RadarTiles>> = MutableStateFlow(UiState.Loading)
    val radarTiles: StateFlow<UiState<RadarTiles>> = _raderTiles

    private val frames = mutableListOf<RadarTiles.Data>()
    private var lastFramePosition = 0
    private val tileOverlays = mutableMapOf<String, TilesOverlay>()
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd E a hh:mm")

    val optionTileSize = 512 // can be 256 or 512.
    val optionColorScheme = 3 // from 0 to 8. Check the https://rainviewer.com/api/color-schemes.html for additional information
    val optionSmoothData = 1 // 0 - not smooth, 1 - smooth
    val optionSnowColors = 1 // 0 - do not show snow colors, 1 - show snow colors

    var animationPosition = 0
    var animationTimer = false

    var latitude = 0.0
    var longitude = 0.0

    var simpleMode = false

    fun initMap() {
        RainViewerRepositoryImpl.initMap(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful) {
                    val responseDto: RainViewerResponseDto = Gson().fromJson(response.body(),
                        RainViewerResponseDto::class.java)
                    _raderTiles.postValue(responseDto)
                } else {
                    //fail
                    _raderTiles.postValue(null)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                _raderTiles.postValue(null)
            }
        })
    }

}