package io.github.pknujsp.weatherwizard.feature.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(230.dp)) {

            AndroidView(modifier = Modifier
                .fillMaxSize(), factory = { context ->
                Configuration.getInstance().run {
                    load(context, PreferenceManager.getDefaultSharedPreferences(context))
                    userAgentValue = context.packageName
                    animationSpeedShort = 230
                    animationSpeedDefault = 230
                }

                MapView(context).apply {
                    clipToOutline = true
                    setBackgroundResource(R.drawable.map_background)

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
                    zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
                    isTilesScaledToDpi = true
                    setOnTouchListener { v, _ ->
                        v.parent.requestDisallowInterceptTouchEvent(true)
                        false
                    }
                }
            }, update = {
                it.onResume()
            })
        }
    })

}


/*
    <org.osmdroid.views.MapView android:id="@+id/map"
                android:layout_width="fill_parent"
                android:layout_height="fill_parent" />
 */