package io.github.pknujsp.weatherwizard.core.network.api.flickr

import io.github.pknujsp.weatherwizard.core.network.datasource.flickr.GetInfoPhotoResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.flickr.PhotosFromGalleryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface FlickrNetworkApi {

    @GET("rest/")
    suspend fun getPhotosFromGallery(@QueryMap(encoded = true) queryMap: Map<String, String>): Response<PhotosFromGalleryResponse>

    @GET("rest/")
    suspend fun getGetInfo(@QueryMap(encoded = true) queryMap: Map<String, String>): Response<GetInfoPhotoResponse>
}