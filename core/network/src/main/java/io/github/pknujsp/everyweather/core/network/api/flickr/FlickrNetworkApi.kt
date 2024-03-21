package io.github.pknujsp.everyweather.core.network.api.flickr

import io.github.pknujsp.everyweather.core.network.retrofit.NetworkApiResult
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface FlickrNetworkApi {
    @GET("rest/")
    suspend fun getPhotosFromGallery(
        @QueryMap(encoded = true) queryMap: Map<String, String>,
    ): NetworkApiResult<PhotosFromGalleryResponse>

    @GET("rest/")
    suspend fun getGetInfo(
        @QueryMap(encoded = true) queryMap: Map<String, String>,
    ): NetworkApiResult<GetInfoPhotoResponse>
}
