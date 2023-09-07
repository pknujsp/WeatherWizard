package io.github.pknujsp.weatherwizard.core.network.datasource.flickr

import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetInfoPhotoResponse(
    @SerialName("photo") val photo: Photo,
    @SerialName("stat") val stat: String = ""
) : ApiResponseModel {

    val imageUrl: String = """
        https://live.staticflickr.com/${photo.server}/${photo.id}_
        ${if (photo.originalSecret.isEmpty()) photo.secret else photo.originalSecret}
        ${if (photo.originalSecret.isEmpty()) "_b.jpg" else "_o.jpg"}
        """.trimIndent()

    @Serializable
    data class Photo(
        @SerialName("id") var id: String = "",
        @SerialName("owner") var owner: String = "",
        @SerialName("secret") var secret: String = "",
        @SerialName("server") var server: String = "",
        @SerialName("originalsecret") var originalSecret: String = "",
        @SerialName("title") var title: String = "",
    )
}