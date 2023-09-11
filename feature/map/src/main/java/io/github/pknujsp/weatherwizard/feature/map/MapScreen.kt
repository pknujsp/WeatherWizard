package io.github.pknujsp.weatherwizard.feature.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MapScreen() {

    SimpleWeatherScreenBackground(CardInfo(title = "기상 레이더") {
        val coroutineScope = rememberCoroutineScope()

        AndroidView(modifier = Modifier
            .fillMaxWidth()
            .clipToBounds()
            .height(200.dp), factory = { context ->
            Configuration.getInstance().run {
                load(context, PreferenceManager.getDefaultSharedPreferences(context))
                userAgentValue = context.packageName
                animationSpeedShort = 250
                animationSpeedDefault = 250
                isEnforceTileSystemBounds = true
            }

            MapView(context).apply {
                setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                overlays.add(MyLocationNewOverlay(GpsMyLocationProvider(context).apply {
                    startLocationProvider { location, _ ->

                    }
                }, this).apply {
                    enableMyLocation()
                    isEnabled = true
                })
                overlays.add(RotationGestureOverlay(this).apply {
                    isEnabled = false
                })

                minZoomLevel = 2.0
                setMultiTouchControls(true)
                isTilesScaledToDpi = true
                zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)

                setOnTouchListener { v, _ ->
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    false
                }
            }
        }, update = {
            it.onResume()
        })
    })
}


/*
    <org.osmdroid.views.MapView android:id="@+id/map"
                android:layout_width="fill_parent"
                android:layout_height="fill_parent" />
 */