package io.github.pknujsp.weatherwizard.feature.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RainViewerViewModel @Inject constructor(
    radarTilesRepository: RadarTilesRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private val _raderTiles: MutableStateFlow<UiState<RadarTilesOverlay>> = MutableStateFlow(UiState.Loading)
    val radarTiles: StateFlow<UiState<RadarTilesOverlay>> = _raderTiles

    private var lastFramePosition = 0
    private val optionTileSize = 512 // can be 256 or 512.
    private val optionColorScheme = 3 // from 0 to 8. Check the https://rainviewer.com/api/color-schemes.html for additional information
    private val optionSmoothData = 1 // 0 - not smooth, 1 - smooth
    private val optionSnowColors = 1 // 0 - do not show snow colors, 1 - show snow colors

    val minZoomLevel: Float = 2f
    val maxZoomLevel: Float = 18f

    init {
        viewModelScope.launch(Dispatchers.IO) {
            radarTilesRepository.getTiles().onSuccess {
                lastFramePosition = it.currentIndex
                val radarTilesOverlay = RadarTilesOverlay(
                    context = context,
                    radarTiles = it,
                    minZoomLevel = minZoomLevel,
                    maxZoomLevel = maxZoomLevel,
                    optionTileSize = optionTileSize,
                    optionColorScheme = optionColorScheme,
                    optionSmoothData = optionSmoothData,
                    optionSnowColors = optionSnowColors
                )
                _raderTiles.value = UiState.Success(radarTilesOverlay)
            }.onFailure {
                _raderTiles.value = UiState.Error(it)
            }
        }
    }


}