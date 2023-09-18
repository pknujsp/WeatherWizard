package io.github.pknujsp.weatherwizard.core.network.api.flickr

import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters

interface FlickrDataSource {
    suspend fun getPhotosFromGallery(getPhotosFromGalleryParameter: FlickrRequestParameters.FlickrGetPhotosFromGalleryParameter): Result<PhotosFromGalleryResponse>

    suspend fun getGetInfo(getInfoParameter: FlickrRequestParameters.FlickrGetInfoParameter): Result<GetInfoPhotoResponse>
}