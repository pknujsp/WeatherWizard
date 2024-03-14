package io.github.pknujsp.everyweather.feature.map.screen

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.everyweather.core.model.onError
import io.github.pknujsp.everyweather.core.model.onSuccess
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.weather.item.CardInfo
import io.github.pknujsp.everyweather.core.ui.weather.item.SimpleWeatherFailedBox
import io.github.pknujsp.everyweather.core.ui.weather.item.SimpleWeatherScreenBackground
import io.github.pknujsp.everyweather.feature.map.OsmdroidInitializer
import io.github.pknujsp.everyweather.feature.map.OsmdroidInitializer.initializeMapView
import io.github.pknujsp.everyweather.feature.map.RadarController
import io.github.pknujsp.everyweather.feature.map.SimpleMapController
import io.github.pknujsp.everyweather.feature.map.model.RadarTilesOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.abs

private const val POI_MARKER_ID = "poi"

private const val DEFAULT_ZOOM = 7.0

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun MapScreen(
    latitude: Double,
    longitude: Double,
    tiles: RadarTilesOverlay,
    timePosition: StateFlow<Int>,
    simpleMapController: SimpleMapController,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    density: Density = LocalDensity.current,
) {
    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
        MapView(context, OsmdroidInitializer.getMapsForgeTileProvider(context)).apply {
            initializeMapView(this)
            simpleMapController.init(this)
            setOnTouchListener { v, _ ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                false
            }
        }
    }, update = { mapView ->
        coroutineScope.launch {
            launch {
                mapView.run {
                    addCurrentLocationPoiMarker(latitude, longitude, density)
                    controller.animateTo(GeoPoint(latitude, longitude), DEFAULT_ZOOM, 0)
                    mapView.onResume()
                }
            }.join()

            launch {
                timePosition.filter { time ->
                    time != -1
                }.collect { time ->
                    mapView.run {
                        if (!overlays.contains(tiles.overlays[time].tilesOverlay)) {
                            val marker = overlays.first { it is Marker }
                            overlays.clear()
                            overlays.add(tiles.overlays[time].run {
                                if (handler.mView == null) {
                                    handler.mView = mapView
                                }
                                tilesOverlay
                            })
                            overlays.add(marker)
                            invalidate()
                        }
                    }

                }
            }
        }
    }, onRelease = { mapView ->
        for (overlay in tiles.overlays) {
            overlay.destroy(mapView)
        }

        mapView.onPause()
        mapView.onDetach()
    })

}

private fun MapView.addCurrentLocationPoiMarker(latitude: Double, longitude: Double, density: Density) {
    overlayManager.overlays().removeIf { (it is Marker) && (it.id == POI_MARKER_ID) }
    Marker(this).run {
        id = POI_MARKER_ID
        position = GeoPoint(latitude, longitude)
        setUseDataConnection(false)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        val iconSize = with(density) { 6.dp.toPx().toInt() }
        icon = BitmapDrawable(resources,
            ResourcesCompat.getDrawable(resources, R.drawable.ic_my_location, null)!!.toBitmap(iconSize, iconSize))
        overlays.add(this)
    }
}

@Composable
fun SimpleMapScreen(
    requestWeatherArguments: RequestWeatherArguments, viewModel: RainViewerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.radarUiState.collectAsStateWithLifecycle()

    LaunchedEffect(requestWeatherArguments) {
        viewModel.load(context)
    }

    uiState.onSuccess {
        SimpleWeatherScreenBackground(cardInfo = CardInfo(title = stringResource(id = R.string.radar)) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val simpleMapController = remember { SimpleMapController() }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)) {
                    MapScreen(requestWeatherArguments.targetLocation.latitude,
                        requestWeatherArguments.targetLocation.longitude,
                        it.overlays,
                        viewModel.timePosition,
                        simpleMapController)
                    MapControllerScreen(simpleMapController, iconSize = 32.dp, bottomSpace = 20.dp)
                }

                RadarControllerScreen(viewModel as RadarController)
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
private fun RadarControllerScreen(controller: RadarController) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {
        val playing by controller.playing.collectAsStateWithLifecycle()

        IconToggleButton(
            modifier = Modifier.size(36.dp),
            checked = false,
            colors = IconButtonDefaults.iconToggleButtonColors(checkedContainerColor = Color.Transparent,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                checkedContentColor = Color.Transparent),
            onCheckedChange = {
                controller.play()
            },
        ) {
            val playIcon by remember {
                derivedStateOf {
                    if (playing) {
                        R.drawable.ic_pause
                    } else {
                        R.drawable.ic_play
                    }
                }
            }

            Icon(painter = painterResource(playIcon), contentDescription = stringResource(id = R.string.play_all_radars))
        }

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            val time by controller.time.collectAsStateWithLifecycle()
            Text(text = time, color = Color.White, fontSize = 13.sp)
            if (playing) {
                CustomLinearProgressIndicator()
            }
        }

        IconButton(
            onClick = {
                controller.beforeRadar()
            },
            modifier = Modifier.size(30.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent, contentColor = Color.White),
        ) {
            Icon(painter = painterResource(R.drawable.ic_previous),
                contentDescription = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.before))
        }
        IconButton(
            onClick = {
                controller.nextRadar()
            },
            modifier = Modifier.size(30.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
            ),
        ) {
            Icon(painter = painterResource(R.drawable.ic_next),
                contentDescription = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.next))
        }
    }
}

@Composable
private fun BoxScope.MapControllerScreen(simpleMapController: SimpleMapController, iconSize: Dp, bottomSpace: Dp) {
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
                contentDescription = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.zoom_in))
        }
        IconButton(onClick = {
            simpleMapController.zoomOut()
        },
            modifier = Modifier.size(iconSize),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White,
                contentColor = Color.Black,
                disabledContentColor = Color.Black)) {
            Icon(painter = painterResource(R.drawable.subtract),
                contentDescription = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.zoom_out))
        }
    }
}

@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    // Fractional position of the 'head' and 'tail' of the two lines drawn, i.e. if the head is 0.8
    // and the tail is 0.2, there is a line drawn from between 20% along to 80% along the total
    // width.
    val firstLineHead = infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(animation = keyframes {
        durationMillis = LinearAnimationDuration
        0f at FirstLineHeadDelay with FirstLineHeadEasing
        1f at FirstLineHeadDuration + FirstLineHeadDelay
    }), label = "")
    val firstLineTail = infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(animation = keyframes {
        durationMillis = LinearAnimationDuration
        0f at FirstLineTailDelay with FirstLineTailEasing
        1f at FirstLineTailDuration + FirstLineTailDelay
    }), label = "")
    val secondLineHead = infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(animation = keyframes {
        durationMillis = LinearAnimationDuration
        0f at SecondLineHeadDelay with SecondLineHeadEasing
        1f at SecondLineHeadDuration + SecondLineHeadDelay
    }), label = "")
    val secondLineTail = infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(animation = keyframes {
        durationMillis = LinearAnimationDuration
        0f at SecondLineTailDelay with SecondLineTailEasing
        1f at SecondLineTailDuration + SecondLineTailDelay
    }), label = "")
    Canvas(modifier
        .progressSemantics()
        .size(LinearIndicatorWidth, LinearIndicatorHeight)) {
        val strokeWidth = size.height
        drawLinearIndicatorTrack(trackColor, strokeWidth, strokeCap)
        if (firstLineHead.value - firstLineTail.value > 0) {
            drawLinearIndicator(
                firstLineHead.value,
                firstLineTail.value,
                color,
                strokeWidth,
                strokeCap,
            )
        }
        if (secondLineHead.value - secondLineTail.value > 0) {
            drawLinearIndicator(
                secondLineHead.value,
                secondLineTail.value,
                color,
                strokeWidth,
                strokeCap,
            )
        }
    }
}

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) {
    val width = size.width
    val height = size.height
    // Start drawing from the vertical center of the stroke
    val yOffset = height / 2

    val isLtr = layoutDirection == LayoutDirection.Ltr
    val barStart = (if (isLtr) startFraction else 1f - endFraction) * width
    val barEnd = (if (isLtr) endFraction else 1f - startFraction) * width

    // if there isn't enough space to draw the stroke caps, fall back to StrokeCap.Butt
    if (strokeCap == StrokeCap.Butt || height > width) {
        // Progress line
        drawLine(color, Offset(barStart, yOffset), Offset(barEnd, yOffset), strokeWidth)
    } else {
        // need to adjust barStart and barEnd for the stroke caps
        val strokeCapOffset = strokeWidth / 2
        val coerceRange = strokeCapOffset..(width - strokeCapOffset)
        val adjustedBarStart = barStart.coerceIn(coerceRange)
        val adjustedBarEnd = barEnd.coerceIn(coerceRange)

        if (abs(endFraction - startFraction) > 0) {
            // Progress line
            drawLine(
                color,
                Offset(adjustedBarStart, yOffset),
                Offset(adjustedBarEnd, yOffset),
                strokeWidth,
                strokeCap,
            )
        }
    }
}

private fun DrawScope.drawLinearIndicatorTrack(
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) = drawLinearIndicator(0f, 1f, color, strokeWidth, strokeCap)

internal val LinearIndicatorWidth = 150.dp

// Indeterminate linear indicator transition specs

// Total duration for one cycle
private const val LinearAnimationDuration = RainViewerViewModel.PLAY_DURATION.toInt()

// Duration of the head and tail animations for both lines
private const val FirstLineHeadDuration = 750
private const val FirstLineTailDuration = 850
private const val SecondLineHeadDuration = 567
private const val SecondLineTailDuration = 533

// Delay before the start of the head and tail animations for both lines
private const val FirstLineHeadDelay = 0
private const val FirstLineTailDelay = 333
private const val SecondLineHeadDelay = 1000
private const val SecondLineTailDelay = 1267

private val FirstLineHeadEasing = CubicBezierEasing(0.2f, 0f, 0.8f, 1f)
private val FirstLineTailEasing = CubicBezierEasing(0.4f, 0f, 1f, 1f)
private val SecondLineHeadEasing = CubicBezierEasing(0f, 0f, 0.65f, 1f)
private val SecondLineTailEasing = CubicBezierEasing(0.1f, 0f, 0.45f, 1f)

private val LinearIndicatorHeight = 4.dp