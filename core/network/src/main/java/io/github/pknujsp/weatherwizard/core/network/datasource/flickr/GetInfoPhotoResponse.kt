package io.github.pknujsp.weatherwizard.core.network.datasource.flickr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetInfoPhotoResponse(
    @SerialName("photo") var photo: PhotosFromGalleryResponse.Photos.Photo,
    @SerialName("stat") val stat: String = ""
)