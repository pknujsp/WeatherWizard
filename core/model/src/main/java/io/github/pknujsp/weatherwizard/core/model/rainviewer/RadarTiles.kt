package io.github.pknujsp.weatherwizard.core.model.rainviewer

import io.github.pknujsp.weatherwizard.core.model.UiModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


data class RadarTiles(
    val generated: Int,
    val host: String,
    val radar: Radar,
    val satellite: Satellite,
    val version: String
) : UiModel {

    data class Radar(
        val nowcast: List<Data>, val past: List<Data>
    )


    data class Satellite(
        val infrared: List<Data>
    )


    data class Data(
        val path: String, val time: Int
    )
}