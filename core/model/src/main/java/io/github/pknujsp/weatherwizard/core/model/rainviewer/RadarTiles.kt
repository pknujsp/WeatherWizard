package io.github.pknujsp.weatherwizard.core.model.rainviewer

import io.github.pknujsp.weatherwizard.core.model.UiModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime


data class RadarTiles(
    val generated: Int,
    val host: String,
    val currentIndex:Int,
    val radar: List<Data>,
    val version: String,
    val requestTime: ZonedDateTime = ZonedDateTime.now()
) : UiModel {

    data class Data(
        val path: String, val time: Int
    )
}