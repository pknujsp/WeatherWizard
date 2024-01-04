package io.github.pknujsp.weatherwizard.feature.map.model

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.UiModel
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Stable
class RadarUiState(
    val radarTileEntities: List<RadarTileEntity>,
    val host: String,
    val defaultIndex: Int,
    baseTime: ZonedDateTime,
) : UiModel {

    companion object {
        private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM.dd E HH:mm")
    }

    val times: List<String>

    init {
        val timeZone = baseTime.zone
        var pointInTime: String
        times = radarTileEntities.mapIndexed { index, it ->
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.time), timeZone).let { time ->
                pointInTime = if (index == defaultIndex) {
                    ""
                } else if (time.isAfter(baseTime)) {
                    "+${Duration.between(baseTime, time).toMinutes()}Min "
                } else {
                    "-${Duration.between(baseTime, time).toMinutes()}Min "
                }
                "$pointInTime${time.format(dateTimeFormatter)}"
            }
        }
    }
}

internal object MapSettingsDefault {
    const val minZoomLevel = 2.0
    const val maxZoomLevel = 19.0
}

internal object RadarTileSettingsDefault {
    const val optionTileSize = 512 // can be 256 or 512.
    const val optionColorScheme = 3
    const val optionSmoothData = 1 // 0 - not smooth, 1 - smooth
    const val optionSnowColors = 1 // 0 - do not show snow colors, 1 - show snow colors
}