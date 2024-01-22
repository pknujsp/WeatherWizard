package io.github.pknujsp.weatherwizard.feature.map.model

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import io.github.pknujsp.weatherwizard.core.model.UiModel
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Stable
class RadarUiState(
    val isRadarTileCached: Boolean,
    val radarTileEntities: List<RadarTileEntity>, val host: String, val defaultIndex: Int, baseTime: ZonedDateTime, context: Context
) : UiModel {

    companion object {
        private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM.dd E HH:mm")
    }

    val overlays = RadarTilesOverlay(isRadarTileCached, context, host, radarTileEntities)

    val times: List<String>

    init {
        val timeZone = baseTime.zone
        var pointInTime: String
        times = radarTileEntities.mapIndexed { index, it ->
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.time), timeZone).let { time ->
                pointInTime = if (index == defaultIndex) {
                    ""
                } else if (time.isAfter(baseTime)) {
                    "+${Duration.between(baseTime, time).toMinutes()} min "
                } else {
                    "${Duration.between(baseTime, time).toMinutes()} min "
                }
                "$pointInTime${time.format(dateTimeFormatter)}"
            }
        }
    }
}

internal object MapSettingsDefault {
    const val MIN_ZOOM_LEVEL = 2.0
    const val MAX_ZOOM_LEVEL = 16.0
}

internal object RadarTileSettingsDefault {
    const val TILE_SIZE = 512 // can be 256 or 512.
    const val COLOR_SCHEME = 3
    const val SMOOTH_DATA = 1 // 0 - not smooth, 1 - smooth
    const val SNOW_COLORS = 1 // 0 - do not show snow colors, 1 - show snow colors

    private val matrix = ColorMatrix().apply {
        setScale(1f, 1f, 1f, 0.75f) // Red, Green, Blue, Alpha
    }

    val ALPHA: ColorFilter = ColorMatrixColorFilter(matrix)
}