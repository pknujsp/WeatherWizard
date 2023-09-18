package io.github.pknujsp.weatherwizard.core.network.api.flickr

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.network.api.flickr.FlickrDataSource
import io.github.pknujsp.weatherwizard.core.network.api.flickr.FlickrDataSourceImpl
import io.github.pknujsp.weatherwizard.core.network.retrofit.NetworkApiCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FlickrNetworkModule {
    private const val flickrUrl = "https://www.flickr.com/services/"

    @Provides
    @Singleton
    fun providesFlickrNetworkApi(okHttpClient: OkHttpClient, @KtJson json: Json): FlickrNetworkApi =
        Retrofit.Builder().client(okHttpClient).baseUrl(flickrUrl).addCallAdapterFactory(NetworkApiCallAdapterFactory())
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            ).build().create(FlickrNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesFlickrDataSource(flickrNetworkApi: FlickrNetworkApi): FlickrDataSource =
        FlickrDataSourceImpl(flickrNetworkApi)
}