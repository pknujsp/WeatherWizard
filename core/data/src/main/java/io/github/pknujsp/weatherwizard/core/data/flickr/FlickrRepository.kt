package io.github.pknujsp.weatherwizard.core.data.flickr

import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrImageEntity
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters

interface FlickrRepository {
    suspend fun getPhoto(parameter: FlickrRequestParameters): Result<FlickrImageEntity>
}