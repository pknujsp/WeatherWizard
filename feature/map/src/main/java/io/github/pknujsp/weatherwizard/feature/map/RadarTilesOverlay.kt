package io.github.pknujsp.weatherwizard.feature.map

import android.content.Context
import android.graphics.Color
import io.github.pknujsp.weatherwizard.core.model.rainviewer.RadarTiles
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.modules.SqlTileWriter
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

    val overlays: List<Pair<TilesOverlay, OverlayHandler>> = radarTiles.run {
        radar.mapIndexed { index, it ->
            val handler = OverlayHandler()
            val tileSourceName = "RadarTiles_$index"
            val tileProvider = MapTileProviderBasic(context,
                object : OnlineTileSourceBase(tileSourceName, minZoomLevel.toInt(), maxZoomLevel.toInt(), optionTileSize, "",
                    emptyArray()) {
                    override fun getTileURLString(pMapTileIndex: Long): String {
                        val x = MapTileIndex.getX(pMapTileIndex)
                        val y = MapTileIndex.getY(pMapTileIndex)
                        val z = MapTileIndex.getZoom(pMapTileIndex)
                        return "$host${it.path}/$optionTileSize/$z/$x/$y/$optionColorScheme/${optionSmoothData}_$optionSnowColors.png"
                    }
                }, SqlTileWriter().apply {
                    purgeCache(tileSourceName)
                }).apply {

                setOfflineFirst(false)
                tileCache.setStressedMemory(true)
                tileRequestCompleteHandlers.add(
                    handler
                )
            }
            TilesOverlay(tileProvider, context).apply {
                loadingBackgroundColor = Color.TRANSPARENT
            } to handler
        }
    }
}