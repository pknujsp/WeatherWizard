package io.github.pknujsp.weatherwizard.core.network.api.flickr

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.network.datasource.flickr.FlickrDataSource
import io.github.pknujsp.weatherwizard.core.network.datasource.flickr.FlickrDataSourceImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FlickrNetworkModule {
    private const val FLICKR_URL = "https://www.flickr.com/services/"

    @Inject lateinit var json: Json

    @Provides
    @Singleton
    fun providesFlickrNetworkApi(okHttpClient: OkHttpClient): FlickrNetworkApi =
        Retrofit.Builder().client(okHttpClient).baseUrl(FLICKR_URL).addConverterFactory(
            json.asConverterFactory(MediaType.get("application/json"))
        ).build().create(FlickrNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesFlickrDataSource(flickrNetworkApi: FlickrNetworkApi): FlickrDataSource =
        FlickrDataSourceImpl(flickrNetworkApi)
}