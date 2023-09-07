package io.github.pknujsp.weatherwizard.core.network.datasource.flickr

import retrofit2.Response

interface FlickrDataSource {
    suspend fun getPhotosFromGallery(getPhotosFromGalleryParameter: FlickrRequestParameters.FlickrGetPhotosFromGalleryParameter): Response<PhotosFromGalleryResponse>

    suspend fun getGetInfo(getInfoParameter: FlickrRequestParameters.FlickrGetInfoParameter): Response<GetInfoPhotoResponse>
}