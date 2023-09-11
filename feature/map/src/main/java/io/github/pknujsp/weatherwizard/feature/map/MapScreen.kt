package io.github.pknujsp.weatherwizard.feature.map

import android.annotation.SuppressLint
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView


@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MapScreen(latitude: Double, longitude: Double, radarAdapter: RadarAdapter, simpleMapController: SimpleMapController) {
    val viewModel = hiltViewModel<RainViewerViewModel>()
    val radarScope = rememberCoroutineScope()
    val adapterScope = rememberCoroutineScope()
    val refreshScope = rememberCoroutineScope()

    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
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
            isHorizontalMapRepetitionEnabled = true
            isVerticalMapRepetitionEnabled = true

            setOnTouchListener { v, _ ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                false
            }

            radarScope.launch {
                viewModel.timePosition.combine(viewModel.radarTiles) { time, tiles -> time to tiles }.collect { (time, tiles) ->
                    tiles.onSuccess {
                        overlays.clear()
                        overlays.add(it.overlays[time])
                        invalidate()
                    }
                }
            }

            adapterScope.launch {
                radarAdapter.setRadarController(SupervisorJob(), viewModel)
            }

            refreshScope.launch {
                viewModel.refresh.collect {
                    invalidate()
                }
            }

            controller.animateTo(org.osmdroid.util.GeoPoint(latitude, longitude), 6.0, 0)
            simpleMapController.init(this)

        }
    }, update = {
        it.onResume()
    })

}


@Composable
fun SimpleMapScreen(requestWeatherDataArgs: () -> StateFlow<UiState<RequestWeatherDataArgs>>) {
    val state by requestWeatherDataArgs().collectAsStateWithLifecycle()

    state.onLoading {
        SimpleWeatherBackgroundPlaceHolder()
    }.onSuccess {
        SimpleWeatherScreenBackground(CardInfo(title = "기상 레이더") {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val radarAdapter = remember { RadarAdapter() }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)) {

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
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
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

        IconToggleButton(checked = false,
            colors = IconButtonDefaults.iconToggleButtonColors(checkedContainerColor = Color.LightGray,
                containerColor = Color.White,
                contentColor = Color.Black,
                checkedContentColor = Color.Black),
            onCheckedChange = {
                radarAdapter.play()
            },
            modifier = Modifier.size(28.dp)) {
            val playIcon by remember {
                derivedStateOf { mutableIntStateOf(io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_baseline_play_arrow_24) }
            }

            LaunchedEffect(Unit) {
                radarAdapter.playing.collect {
                    playIcon.intValue = if (it) {
                        io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_baseline_stop_24
                    } else {
                        io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_baseline_play_arrow_24
                    }
                }
            }

            Icon(painter = painterResource(playIcon.intValue), contentDescription = stringResource(id = R.string.play_all_radars))
        }

        Text(text = time.value, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))

        IconButton(onClick = {
            radarAdapter.beforeRadar()
        },
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White, contentColor = Color.Black),
            modifier = Modifier.size(28.dp)) {
            Icon(painter = painterResource(io.github.pknujsp.weatherwizard.core.common.R.drawable.baseline_navigate_before_24),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.before))
        }
        IconButton(onClick = {
            radarAdapter.nextRadar()
        },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
            ),
            modifier = Modifier.size(28.dp)) {
            Icon(painter = painterResource(io.github.pknujsp.weatherwizard.core.common.R.drawable.baseline_navigate_next_24),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.next))
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