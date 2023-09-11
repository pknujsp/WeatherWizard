package io.github.pknujsp.weatherwizard.feature.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.preference.PreferenceManager
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherBackgroundPlaceHolder
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView


@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MapScreen(requestWeatherDataArgs: () -> StateFlow<UiState<RequestWeatherDataArgs>>) {
    val state by requestWeatherDataArgs().collectAsStateWithLifecycle()

    state.onLoading {
        SimpleWeatherBackgroundPlaceHolder()
    }.onSuccess {
        SimpleWeatherScreenBackground(CardInfo(title = "기상 레이더") {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(230.dp)) {
                val viewModel = hiltViewModel<RainViewerViewModel>()
                val coroutineScope = rememberCoroutineScope()

                AndroidView(modifier = Modifier
                    .fillMaxSize(), factory = { context ->
                    Configuration.getInstance().run {
                        load(context, PreferenceManager.getDefaultSharedPreferences(context))
                        userAgentValue = context.packageName
                        animationSpeedShort = 230
                        animationSpeedDefault = 230
                        isMapViewHardwareAccelerated = true
                    }

                    MapView(context).apply {
                        clipToOutline = true
                        setBackgroundResource(R.drawable.map_background)
                        setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)

                        /*
                        overlays.add(MyLocationNewOverlay(GpsMyLocationProvider(context).apply {
                            startLocationProvider { location, _ ->

                            }
                        }, this).apply {
                            enableMyLocation()
                            isEnabled = true
                        })

                         */
                        maxZoomLevel = viewModel.maxZoomLevel.toDouble()
                        minZoomLevel = viewModel.minZoomLevel.toDouble()
                        setMultiTouchControls(true)
                        zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
                        isTilesScaledToDpi = false
                        isHorizontalMapRepetitionEnabled = false
                        isVerticalMapRepetitionEnabled = false

                        setOnTouchListener { v, _ ->
                            v.parent.requestDisallowInterceptTouchEvent(true)
                            false
                        }

                        coroutineScope.launch {
                            viewModel.radarTiles.collect { overlayUiState ->
                                overlayUiState.onSuccess {
                                    overlays.add(it.overlays[it.currentIndex])
                                }
                            }
                        }

                        controller.animateTo(org.osmdroid.util.GeoPoint(it.latitude, it.longitude), 6.0, 0)
                    }
                }, update = {
                    it.onResume()
                })
            }

        })
    }

}