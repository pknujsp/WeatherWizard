package io.github.pknujsp.weatherwizard.feature.map

import android.content.Context
import io.github.pknujsp.weatherwizard.core.model.rainviewer.RadarTiles
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.overlay.TilesOverlay
import java.time.Instant
import java.time.ZonedDateTime

class RadarTilesOverlay(
    context: Context,
    radarTiles: RadarTiles,
    minZoomLevel: Float,
    maxZoomLevel: Float,
    optionTileSize: Int,
    optionColorScheme: Int,
    optionSmoothData: Int,
    optionSnowColors: Int,
    requestTime: ZonedDateTime,
    val currentIndex: Int = radarTiles.currentIndex
) {

    val times: List<String> = radarTiles.radar.run {
        val timeZone = requestTime.zone
        val dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("M/d E HH:mm")
        val past = context.getString(R.string.past)
        val now = context.getString(R.string.now)
        val future = context.getString(R.string.future)

        var pointInTime: String
        mapIndexed { index, it ->
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.time.toLong()), timeZone).let { time ->
                pointInTime = if (index == currentIndex) now
                else if (time.isAfter(requestTime)) future
                else past
                "$pointInTime ${time.format(dateTimeFormatter)}"
            }
        }
    }

    val overlays: List<TilesOverlay> = radarTiles.run {
        radar.mapIndexed { index, it ->
            val customTileSource =
                object : OnlineTileSourceBase("RadarTiles_$index", minZoomLevel.toInt(), maxZoomLevel.toInt(), optionTileSize, ".png",
                    emptyArray()) {

                    override fun getTileURLString(pMapTileIndex: Long): String {
                        val x = MapTileIndex.getX(pMapTileIndex)
                        val y = MapTileIndex.getY(pMapTileIndex)
                        val zoom = MapTileIndex.getZoom(pMapTileIndex)

                        return "${host}${it.path}/$optionTileSize/$zoom/$x/$y/$optionColorScheme/${optionSmoothData}_$optionSnowColors.png"
                    }

                    // https://tilecache.rainviewer.com/v2/satellite/689559864b21/512/2/2/1/3/1_1.png
                }

            val tileProvider = MapTileProviderBasic(context).apply {
                tileSource = customTileSource
            }
            TilesOverlay(tileProvider, context).apply {
                isEnabled = true
            }
        }
    }
}