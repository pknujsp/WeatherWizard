package io.github.pknujsp.weatherwizard.feature.map

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import io.github.pknujsp.weatherwizard.feature.map.model.RadarTilesOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow


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
) {/*
        AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
            (context).apply {
                clipToOutline = true
                setBackgroundResource(R.drawable.map_background)

            }
        }, update = { mapView ->

        }, onRelease = { mapView ->

        })
    */

}