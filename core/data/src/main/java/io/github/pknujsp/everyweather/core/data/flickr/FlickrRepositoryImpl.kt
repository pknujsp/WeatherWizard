package io.github.pknujsp.everyweather.core.data.flickr

import android.util.LruCache
import io.github.pknujsp.everyweather.core.model.flickr.FlickrImageEntity
import io.github.pknujsp.everyweather.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.everyweather.core.network.api.flickr.FlickrDataSource

class FlickrRepositoryImpl(
    private val flickrDataSource: FlickrDataSource,
) : FlickrRepository {
    private val imageUrlMap = LruCache<String, String>(10)

    override suspend fun getPhoto(parameter: FlickrRequestParameters): Result<FlickrImageEntity> {
        val getPhotosFromGalleryParameter = parameter.toGetPhotosFromGalleryParameter()
        if (imageUrlMap[getPhotosFromGalleryParameter.galleryId] != null) {
            return Result.success(FlickrImageEntity(imageUrlMap[getPhotosFromGalleryParameter.galleryId]))
        }

        val photos = flickrDataSource.getPhotosFromGallery(getPhotosFromGalleryParameter)
        if (photos.isFailure) {
            return Result.failure(Throwable())
        }

        val photo = photos.getOrNull()!!.photos.photo[(0 until photos.getOrNull()!!.photos.photo.size).random()]
        val getInfoParameter = parameter.toGetInfoParameter(photo.id, photo.secret)

        return flickrDataSource.getGetInfo(getInfoParameter).map {
            val imageUrl = it.imageUrl
            imageUrlMap.put(getPhotosFromGalleryParameter.galleryId, imageUrl)
            FlickrImageEntity(imageUrl)
        }
    }
}
