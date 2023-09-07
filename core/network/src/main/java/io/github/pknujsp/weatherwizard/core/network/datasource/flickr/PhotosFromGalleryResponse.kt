package io.github.pknujsp.weatherwizard.core.network.datasource.flickr

import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosFromGalleryResponse(
    @SerialName("photos") val photos: Photos, @SerialName("stat") val stat: String = ""
) : ApiResponseModel {

    @Serializable
    data class Photos(
        @SerialName("page") val page: String = "",
        @SerialName("pages") var pages: String = "",
        @SerialName("perpage") var perPage: String = "",
        @SerialName("total") var total: String = "",
        @SerialName("photo") var photo: List<Photo>,
    ) {
        @Serializable
        data class Photo(
            @SerialName("id") var id: String = "",
            @SerialName("owner") var owner: String = "",
            @SerialName("secret") var secret: String = "",
            @SerialName("server") var server: String = "",
            @SerialName("title") var title: String = "",
        )
    }

}