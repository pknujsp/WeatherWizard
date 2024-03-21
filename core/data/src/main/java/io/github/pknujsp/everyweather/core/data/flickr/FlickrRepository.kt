package io.github.pknujsp.everyweather.core.data.flickr

import io.github.pknujsp.everyweather.core.model.flickr.FlickrImageEntity
import io.github.pknujsp.everyweather.core.model.flickr.FlickrRequestParameters

interface FlickrRepository {
    suspend fun getPhoto(parameter: FlickrRequestParameters): Result<FlickrImageEntity>
}
