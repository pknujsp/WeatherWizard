package io.github.pknujsp.everyweather.core.network.api.flickr

import io.github.pknujsp.everyweather.core.model.ApiResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosFromGalleryResponse(
    @SerialName("photos") val photos: Photos,
    @SerialName("stat") val stat: String = "",
) : ApiResponseModel {
    @Serializable
    data class Photos(
        @SerialName("page") val page: Int = 0,
        @SerialName("pages") val pages: Int = 0,
        @SerialName("perpage") val perPage: Int = 0,
        @SerialName("total") val total: Int = 0,
        @SerialName("photo") val photo: List<Photo> = emptyList(),
    ) {
        @Serializable
        data class Photo(
            @SerialName("id") val id: String = "",
            @SerialName("owner") val owner: String = "",
            @SerialName("secret") val secret: String = "",
            @SerialName("server") val server: String = "",
            @SerialName("title") val title: String = "",
            @SerialName("farm") val farm: Int = 0,
            @SerialName("ispublic") val isPublic: Int = 0,
            @SerialName("isfriend") val isFriend: Int = 0,
            @SerialName("isfamily") val isFamily: Int = 0,
            @SerialName("is_primary") val isPrimary: Int = 0,
            @SerialName("has_comment") val hasComment: Int = 0,
        )
    }
}
