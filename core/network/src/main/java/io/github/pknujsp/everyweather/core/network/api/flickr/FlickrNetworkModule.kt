package io.github.pknujsp.everyweather.core.network.api.flickr

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.everyweather.core.common.module.KtJson
import io.github.pknujsp.everyweather.core.network.retrofit.NetworkApiCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FlickrNetworkModule {
    private const val URL = "https://www.flickr.com/services/"

    @Provides
    @Singleton
    fun providesFlickrNetworkApi(
        okHttpClient: OkHttpClient,
        @KtJson json: Json,
    ): FlickrNetworkApi = Retrofit.Builder().client(okHttpClient).baseUrl(URL).addCallAdapterFactory(NetworkApiCallAdapterFactory())
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build().create(FlickrNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesFlickrDataSource(flickrNetworkApi: FlickrNetworkApi): FlickrDataSource = FlickrDataSourceImpl(flickrNetworkApi)
}