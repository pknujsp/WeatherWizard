package io.github.pknujsp.everyweather.feature.map.model

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.feature.map.overlay.OverlayHandler
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.modules.SqlTileWriter
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay

@Stable
class RadarTilesOverlay(
    isRadarTileCached: Boolean,
    context: Context,
    host: String,
    radarTiles: List<RadarTileEntity>,
) : UiModel {
    val overlays: List<OverlayItem> =
        radarTiles.run {
            mapIndexed { index, it ->
                val handler = OverlayHandler()
                val tileSourceName = "RadarTile_$index"

                val tileProvider =
                    MapTileProviderBasic(
                        context,
                        object : OnlineTileSourceBase(
                            tileSourceName,
                            MapSettingsDefault.MIN_ZOOM_LEVEL.toInt(),
                            MapSettingsDefault.MAX_ZOOM_LEVEL.toInt(),
                            RadarTileSettingsDefault.TILE_SIZE,
                            "",
                            emptyArray(),
                        ) {
                            override fun getTileURLString(pMapTileIndex: Long): String {
                                val x = MapTileIndex.getX(pMapTileIndex)
                                val y = MapTileIndex.getY(pMapTileIndex)
                                val z = MapTileIndex.getZoom(pMapTileIndex)

                                return "$host${it.path}/${RadarTileSettingsDefault.TILE_SIZE}/$z/$x/$y/${RadarTileSettingsDefault.COLOR_SCHEME}/${RadarTileSettingsDefault.SMOOTH_DATA}_${RadarTileSettingsDefault.SNOW_COLORS}.png"
                            }
                        },
                        SqlTileWriter().apply {
                            if (!isRadarTileCached) {
                                purgeCache()
                            }
                        },
                    ).apply {
                        tileRequestCompleteHandlers.add(handler)
                        setOfflineFirst(false)
                    }

                val tilesOverlay =
                    TilesOverlay(tileProvider, context).apply {
                        loadingBackgroundColor = Color.TRANSPARENT
                        setColorFilter(RadarTileSettingsDefault.ALPHA)
                    }
                OverlayItem(tilesOverlay, handler)
            }
        }

    class OverlayItem(
        val tilesOverlay: TilesOverlay,
        val handler: OverlayHandler,
    ) {
        fun destroy(mapView: MapView) {
            tilesOverlay.onPause()
            tilesOverlay.onDetach(mapView)
            handler.destroy()
        }
    }
}
