package io.github.pknujsp.weatherwizard.core.network.api.nominatim

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.network.api.flickr.FlickrNetworkApi
import io.github.pknujsp.weatherwizard.core.network.datasource.flickr.FlickrDataSource
import io.github.pknujsp.weatherwizard.core.network.datasource.flickr.FlickrDataSourceImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NominatimNetworkModule {
    private const val NOMINATIM_URL = "https://nominatim.openstreetmap.org/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun providesNominatimNetworkApi(okHttpClient: OkHttpClient): NominatimNetworkApi =
        Retrofit.Builder().client(okHttpClient).baseUrl(NOMINATIM_URL).addConverterFactory(
            json.asConverterFactory(MediaType.get("application/json"))
        ).build().create(NominatimNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesNominatimDataSource(flickrNetworkApi: FlickrNetworkApi): FlickrDataSource =
        FlickrDataSourceImpl(flickrNetworkApi)
}