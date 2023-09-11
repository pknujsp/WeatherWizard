package io.github.pknujsp.weatherwizard.core.network.api.rainviewer

import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RainViewerResponse(
    @SerialName("generated") val generated: Int,
    @SerialName("host") val host: String,
    @SerialName("radar") val radar: Radar,
    @SerialName("satellite") val satellite: Satellite,
    @SerialName("version") val version: String
) : ApiResponseModel {
    @Serializable
    data class Radar(
        @SerialName("nowcast") val nowcast: List<Data>, @SerialName("past") val past: List<Data>
    )

    @Serializable
    data class Satellite(
        @SerialName("infrared") val infrared: List<Data>
    )

    @Serializable
    data class Data(
        @SerialName("path") val path: String, @SerialName("time") val time: Int
    )
}