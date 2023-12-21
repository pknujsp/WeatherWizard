package io.github.pknujsp.weatherwizard.core.network.api.metnorway

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.network.retrofit.NetworkApiCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MetNorwayNetworkModule {
    private const val URL = "https://api.met.no/weatherapi/"

    @Provides
    @Singleton
    fun providesNetworkApi(okHttpClient: OkHttpClient, @KtJson json: Json): MetNorwayNetworkApi =
        Retrofit.Builder().client(okHttpClient).baseUrl(URL)
            .addCallAdapterFactory(NetworkApiCallAdapterFactory())
            .addConverterFactory(
                ScalarsConverterFactory.create()
            )
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            ).build().create(MetNorwayNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesMetNorwayDataSource(api: MetNorwayNetworkApi): MetNorwayDataSource =
        MetNorwayDataSourceImpl(api)
}