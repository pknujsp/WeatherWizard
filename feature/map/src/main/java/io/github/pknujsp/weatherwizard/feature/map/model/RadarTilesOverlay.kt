package io.github.pknujsp.weatherwizard.feature.map.model

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.feature.map.OverlayHandler
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.modules.SqlTileWriter
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.TilesOverlay.INVERT_COLORS

@Stable
class RadarTilesOverlay(
    context: Context,
    host: String,
    radarTiles: List<RadarTileEntity>,
) : UiModel {
    val overlays: List<Pair<TilesOverlay, OverlayHandler>> = radarTiles.run {
        mapIndexed { index, it ->
            val handler = OverlayHandler()
            val tileSourceName = "RadarTile_$index"

            val tileProvider = MapTileProviderBasic(context,
                object : OnlineTileSourceBase(tileSourceName,
                    MapSettingsDefault.MIN_ZOOM_LEVEL.toInt(),
                    MapSettingsDefault.MAX_ZOOM_LEVEL.toInt(),
                    RadarTileSettingsDefault.TILE_SIZE,
                    "",
                    emptyArray()) {
                    override fun getTileURLString(pMapTileIndex: Long): String {
                        val x = MapTileIndex.getX(pMapTileIndex)
                        val y = MapTileIndex.getY(pMapTileIndex)
                        val z = MapTileIndex.getZoom(pMapTileIndex)
                        return "$host${it.path}/${RadarTileSettingsDefault.TILE_SIZE}/$z/$x/$y/${RadarTileSettingsDefault.COLOR_SCHEME}/${RadarTileSettingsDefault.SMOOTH_DATA}_${RadarTileSettingsDefault.SNOW_COLORS}.png"
                    }
                },
                SqlTileWriter().apply {
                    purgeCache(tileSourceName)
                }).apply {
                setOfflineFirst(false)
                tileCache.setStressedMemory(true)
                tileRequestCompleteHandlers.add(handler)
            }
            TilesOverlay(tileProvider, context).apply {
                loadingBackgroundColor = Color.TRANSPARENT
                setColorFilter(RadarTileSettingsDefault.ALPHA)
            } to handler
        }
    }

}