package io.github.pknujsp.weatherwizard.core.model.rainviewer

import io.github.pknujsp.weatherwizard.core.model.UiModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


data class RadarTiles(
    val generated: Int,
    val host: String,
    val currentIndex:Int,
    val radar: List<Data>,
    val version: String
) : UiModel {

    data class Data(
        val path: String, val time: Int
    )
}