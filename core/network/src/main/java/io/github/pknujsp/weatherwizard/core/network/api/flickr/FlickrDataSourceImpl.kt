package io.github.pknujsp.weatherwizard.core.network.api.flickr

import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult

internal class FlickrDataSourceImpl(
    private val flickrNetworkApi: FlickrNetworkApi
) : FlickrDataSource {
    override suspend fun getPhotosFromGallery(getPhotosFromGalleryParameter: FlickrRequestParameters.FlickrGetPhotosFromGalleryParameter) =
        flickrNetworkApi.getPhotosFromGallery(getPhotosFromGalleryParameter.map).onResult()

    override suspend fun getGetInfo(getInfoParameter: FlickrRequestParameters.FlickrGetInfoParameter) =
        flickrNetworkApi.getGetInfo(getInfoParameter.map).onResult()
}