package io.github.pknujsp.weatherwizard.feature.map

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherFailedBox
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import io.github.pknujsp.weatherwizard.core.resource.R

private const val POI_MARKER_ID = "poi"

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun MapScreen(
    latitude: Double, longitude: Double, tiles: RadarTilesOverlay, viewModel: RainViewerViewModel, simpleMapController: SimpleMapController
) {
    val radarScope = rememberCoroutineScope()
    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
        MapView(context).apply {
            clipToOutline = true
            setBackgroundResource(R.drawable.map_background)
            setTileSource(TileSourceFactory.MAPNIK)

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

            poiOnMap(latitude, longitude, 18.dp)
            controller.animateTo(GeoPoint(latitude, longitude), 6.0, 0)
            simpleMapController.init(this)
        }
    }, update = { mapView ->
        mapView.onResume()
        radarScope.launch {
            viewModel.timePosition.filter { time -> time != -1 }.collect { time ->
                if (!(mapView.overlays.isNotEmpty() and mapView.overlays.contains(tiles.overlays[time].first))) {
                    mapView.overlays.removeIf { it !is Marker }
                    mapView.overlays.add(tiles.overlays[time].let {
                        if (it.second.mView == null) it.second.mView = mapView
                        it.first
                    })
                    mapView.invalidate()
                }
            }
        }
    }, onRelease = { mapView ->
        viewModel.radarTiles.value.onSuccess { tilesOverlay ->
            tilesOverlay.overlays.forEach { it.second.destroy() }
        }
        mapView.tileProvider.clearTileCache()
        mapView.onPause()
        mapView.onDetach()
    })

}

private fun MapView.poiOnMap(latitude: Double, longitude: Double, iconSize: Dp) {
    Marker(this).apply {
        id = POI_MARKER_ID
        position = GeoPoint(latitude, longitude)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_my_location, null)!!.toBitmap()
            .scale(iconSize.value.toInt(), iconSize.value.toInt(), true).run {
                BitmapDrawable(resources, this)
            }
        overlays.add(this)
    }
}

@Composable
fun SimpleMapScreen(
    requestWeatherArguments: RequestWeatherArguments, viewModel: RainViewerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tiles by viewModel.radarTiles.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load(context)
    }

    tiles.onSuccess {
        SimpleWeatherScreenBackground(CardInfo(title = stringResource(id = R.string.radar)) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val adapterScope = rememberCoroutineScope()
                val radarAdapter = remember {
                    RadarAdapter().apply {
                        setRadarController(adapterScope, viewModel)
                    }
                }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)) {
                    val simpleMapController = remember { SimpleMapController() }
                    MapScreen(requestWeatherArguments.location.latitude,
                        requestWeatherArguments.location.longitude,
                        it,
                        viewModel,
                        simpleMapController)
                    MapControllerScreen(simpleMapController, iconSize = 32.dp, bottomSpace = 20.dp)
                }

                RadarControllerScreen(radarAdapter) {

                }
            }
        })
    }.onError {
        SimpleWeatherFailedBox(title = stringResource(id = R.string.radar),
            description = stringResource(id = R.string.failed_to_load_radar)) {
            viewModel.load(context)
        }
    }

}

@Composable
private fun RadarControllerScreen(radarAdapter: RadarAdapter, onChangedPlay: (Boolean) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()) {
        val time by radarAdapter.time.collectAsStateWithLifecycle()

        IconToggleButton(
            modifier = Modifier.size(36.dp),
            checked = false,
            colors = IconButtonDefaults.iconToggleButtonColors(checkedContainerColor = Color.Transparent,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                checkedContentColor = Color.Transparent),
            onCheckedChange = {
                radarAdapter.play()
            },
        ) {
            var playIcon by remember {
                mutableStateOf(R.drawable.ic_play to false)
            }

            val playing by radarAdapter.playing.collectAsStateWithLifecycle()

            LaunchedEffect(playing) {
                onChangedPlay(playing)
                playIcon = (if (playing) R.drawable.ic_pause else R.drawable.ic_play) to playing
            }

            Icon(painter = painterResource(playIcon.first), contentDescription = stringResource(id = R.string.play_all_radars))
        }

        Text(text = time, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))

        IconButton(
            onClick = {
                radarAdapter.beforeRadar()
            },
            modifier = Modifier.size(30.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent, contentColor = Color.White),
        ) {
            Icon(painter = painterResource(R.drawable.ic_previous),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.before))
        }
        IconButton(
            onClick = {
                radarAdapter.nextRadar()
            },
            modifier = Modifier.size(30.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
            ),
        ) {
            Icon(painter = painterResource(R.drawable.ic_next),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.next))
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
            Icon(painter = painterResource(R.drawable.add),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.zoom_in))
        }
        IconButton(onClick = {
            simpleMapController.zoomOut()
        },
            modifier = Modifier.size(iconSize),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White,
                contentColor = Color.Black,
                disabledContentColor = Color.Black)) {
            Icon(painter = painterResource(R.drawable.subtract),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.zoom_out))
        }
    }
}