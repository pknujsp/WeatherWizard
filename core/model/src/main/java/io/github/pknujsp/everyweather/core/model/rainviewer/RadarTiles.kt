package io.github.pknujsp.everyweather.core.model.rainviewer

import io.github.pknujsp.everyweather.core.model.EntityModel
import java.time.ZonedDateTime

data class RadarTiles(
    val generated: Int,
    val host: String,
    val currentIndex: Int,
    val radar: List<Data>,
    val version: String,
    val requestTime: ZonedDateTime = ZonedDateTime.now(),
) : EntityModel {
    data class Data(
        val path: String,
        val time: Int,
    )
}
