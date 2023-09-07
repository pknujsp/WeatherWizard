package io.github.pknujsp.weatherwizard.core.network.datasource.flickr

import io.github.pknujsp.weatherwizard.core.network.api.flickr.FlickrNetworkApi
import javax.inject.Inject

class FlickrDataSourceImpl @Inject constructor(
    private val flickrNetworkApi: FlickrNetworkApi
) : FlickrDataSource {
    override suspend fun getPhotosFromGallery(getPhotosFromGalleryParameter: FlickrRequestParameters.FlickrGetPhotosFromGalleryParameter) =
        flickrNetworkApi.getPhotosFromGallery(getPhotosFromGalleryParameter.map).run {
            if (isSuccessful) {
                Result.success(body()!!)
            } else {
                Result.failure(Exception(errorBody()?.string() ?: "Unknown error"))
            }
        }

    override suspend fun getGetInfo(getInfoParameter: FlickrRequestParameters.FlickrGetInfoParameter) =
        flickrNetworkApi.getGetInfo(getInfoParameter.map).run {
            if (isSuccessful) {
                Result.success(body()!!)
            } else {
                Result.failure(Exception(errorBody()?.string() ?: "Unknown error"))
            }
        }
}