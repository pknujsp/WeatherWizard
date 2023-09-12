package io.github.pknujsp.weatherwizard.core.network.api.flickr

import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.weatherwizard.core.network.api.flickr.FlickrNetworkApi
import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult
import io.github.pknujsp.weatherwizard.core.network.retrofit.onSuccess
import javax.inject.Inject

class FlickrDataSourceImpl @Inject constructor(
    private val flickrNetworkApi: FlickrNetworkApi
) : FlickrDataSource {
    override suspend fun getPhotosFromGallery(getPhotosFromGalleryParameter: FlickrRequestParameters.FlickrGetPhotosFromGalleryParameter) =
        flickrNetworkApi.getPhotosFromGallery(getPhotosFromGalleryParameter.map).onResult()

    override suspend fun getGetInfo(getInfoParameter: FlickrRequestParameters.FlickrGetInfoParameter) =
        flickrNetworkApi.getGetInfo(getInfoParameter.map).onResult()
}