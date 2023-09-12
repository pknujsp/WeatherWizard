package io.github.pknujsp.weatherwizard.core.data.flickr

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.network.api.flickr.FlickrDataSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FlickrRepositoryModule {

    @Provides
    @Singleton
    fun providesFlickrRepository(
        flickrDataSource: FlickrDataSource
    ): FlickrRepository = FlickrRepositoryImpl(flickrDataSource)

}