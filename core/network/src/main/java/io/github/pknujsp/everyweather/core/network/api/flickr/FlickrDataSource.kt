package io.github.pknujsp.everyweather.core.network.api.flickr

import io.github.pknujsp.everyweather.core.model.flickr.FlickrRequestParameters

interface FlickrDataSource {
    suspend fun getPhotosFromGallery(getPhotosFromGalleryParameter: FlickrRequestParameters.FlickrGetPhotosFromGalleryParameter): Result<PhotosFromGalleryResponse>

    suspend fun getGetInfo(getInfoParameter: FlickrRequestParameters.FlickrGetInfoParameter): Result<GetInfoPhotoResponse>
}