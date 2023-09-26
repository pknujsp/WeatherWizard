package io.github.pknujsp.weatherwizard.feature.map

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private const val POI_MARKER_ID = "poi"

@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MapScreen(latitude: Double, longitude: Double, radarAdapter: RadarAdapter, simpleMapController: SimpleMapController) {
    val viewModel = hiltViewModel<RainViewerViewModel>()
    val radarScope = rememberCoroutineScope()
    val adapterScope = rememberCoroutineScope()

    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
        Configuration.getInstance().run {
            userAgentValue = context.packageName
            animationSpeedShort = 200
            animationSpeedDefault = 200
            cacheMapTileOvershoot = (12).toShort()
            cacheMapTileCount = (12).toShort()
        }

        MapView(context).apply {
            clipToOutline = true
            setBackgroundResource(R.drawable.map_background)
            setTileSource(MAPNIK)

            tileProvider.tileCache.setStressedMemory(true)
            tileProvider.tileCache.ensureCapacity(100)

            maxZoomLevel = viewModel.maxZoomLevel.toDouble()
            minZoomLevel = viewModel.minZoomLevel.toDouble()
            setMultiTouchControls(true)
            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)

            isTilesScaledToDpi = true
            isHorizontalMapRepetitionEnabled = true
            isVerticalMapRepetitionEnabled = true

            setOnTouchListener { v, _ ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                false
            }

            radarAdapter.setRadarController(adapterScope, viewModel)
            poiOnMap(latitude, longitude)
            controller.animateTo(GeoPoint(latitude, longitude), 6.0, 0)
            simpleMapController.init(this)
        }
    }, update = { mapView ->
        mapView.onResume()
        radarScope.launch {
            viewModel.timePosition.combine(viewModel.radarTiles) { time, tiles -> time to tiles }.distinctUntilChanged()
                .filter { (time, tiles) -> time != -1 }.collect { (time, tiles) ->
                    tiles.onSuccess { radarTilesOverlay ->
                        if (!(mapView.overlays.isNotEmpty() and mapView.overlays.contains(radarTilesOverlay.overlays[time].first))) {
                            mapView.overlays.removeIf { it !is Marker }
                            mapView.overlays.add(radarTilesOverlay.overlays[time].let {
                                if (it.second.mView == null) it.second.mView = mapView
                                it.first
                            })
                            mapView.invalidate()
                        }
                    }
                }
        }

    })

}

private fun MapView.poiOnMap(latitude: Double, longitude: Double) {
    Marker(this).apply {
        id = POI_MARKER_ID
        position = GeoPoint(latitude, longitude)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        setDefaultIcon()
        icon = ResourcesCompat.getDrawable(resources, io.github.pknujsp.weatherwizard.core.common.R.drawable.poi_on_map, null)!!.also {
            it.setTintList(ColorStateList.valueOf(Color.Blue.toArgb()))
        }
        overlays.add(this)
    }
}

@Composable
fun SimpleMapScreen(requestWeatherDataArgs: () -> StateFlow<UiState<RequestWeatherDataArgs>>) {
    val state by requestWeatherDataArgs().collectAsStateWithLifecycle()

    state.onLoading {

    }.onSuccess {
        SimpleWeatherScreenBackground(CardInfo(title = "기상 레이더") {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val radarAdapter = remember { RadarAdapter() }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(235.dp)) {

                    val simpleMapController = remember { SimpleMapController() }

                    MapScreen(it.latitude, it.longitude, radarAdapter, simpleMapController)
                    MapControllerScreen(simpleMapController, iconSize = 32.dp, bottomSpace = 24.dp)
                }
                RadarControllerScreen(radarAdapter)
            }
        })
    }
}

@Composable
private fun RadarControllerScreen(radarAdapter: RadarAdapter) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()) {
        val time by remember { derivedStateOf { mutableStateOf("") } }
        LaunchedEffect(Unit) {
            radarAdapter.time.collect {
                time.value = it
            }
        }



        IconToggleButton(
            modifier = Modifier.size(32.dp),
            checked = false,
            colors = IconButtonDefaults.iconToggleButtonColors(checkedContainerColor = Color.Transparent,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                checkedContentColor = Color.Transparent),
            onCheckedChange = {
                radarAdapter.play()
            },
        ) {
            val playIcon by remember {
                derivedStateOf { mutableStateOf(io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_baseline_play_arrow_24 to false) }
            }

            LaunchedEffect(Unit) {
                radarAdapter.playing.collect {
                    playIcon.value = if (it) {
                        io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_baseline_stop_24 to true
                    } else {
                        io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_baseline_play_arrow_24 to false
                    }
                }
            }

            Icon(painter = painterResource(playIcon.value.first), contentDescription = stringResource(id = R.string.play_all_radars))
            if (playIcon.value.second) {
                val infiniteTransition = rememberInfiniteTransition("PlayingButton")
                val angle by infiniteTransition.animateFloat(initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
                    label = "PlayingButton")

                Image(painter = painterResource(R.drawable.round_loading_circle_orange),
                    contentDescription = stringResource(id = R.string.play_all_radars),
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(angle)
                        .scale(1.5f)
                        .background(Color.Transparent))
            }
        }

        Text(text = time.value, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))

        IconButton(
            modifier = Modifier.size(32.dp),
            onClick = {
                radarAdapter.beforeRadar()
            },
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent, contentColor = Color.White),
        ) {
            Icon(painter = painterResource(io.github.pknujsp.weatherwizard.core.common.R.drawable.round_arrow_left_24),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.before),
                modifier = Modifier.scale(1.5f))
        }
        IconButton(
            onClick = {
                radarAdapter.nextRadar()
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
            ),
        ) {
            Icon(painter = painterResource(io.github.pknujsp.weatherwizard.core.common.R.drawable.round_arrow_right_24),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.next),
                modifier = Modifier.scale(1.5f))
        }
    }
}

@Composable
private fun BoxScope.MapControllerScreen(simpleMapController: SimpleMapController, iconSize: Dp, bottomSpace: Dp) {
    // zoom in, zoom out
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(bottom = bottomSpace, end = 24.dp)
            .align(Alignment.BottomEnd)
            .wrapContentSize()) {
        IconButton(onClick = {
            simpleMapController.zoomIn()
        },
            modifier = Modifier.size(iconSize),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White, contentColor = Color.Black)) {
            Icon(painter = painterResource(io.github.pknujsp.weatherwizard.core.common.R.drawable.add),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.zoom_in))
        }
        IconButton(onClick = {
            simpleMapController.zoomOut()
        },
            modifier = Modifier.size(iconSize),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White,
                contentColor = Color.Black,
                disabledContentColor = Color.Black)) {
            Icon(painter = painterResource(io.github.pknujsp.weatherwizard.core.common.R.drawable.subtract),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.zoom_out))
        }
    }
}