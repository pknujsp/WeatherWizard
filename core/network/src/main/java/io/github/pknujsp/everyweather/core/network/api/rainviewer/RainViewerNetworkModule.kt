package io.github.pknujsp.everyweather.core.network.api.rainviewer

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
object RainViewerNetworkModule {
    private const val RAIN_VIEWER_URL = "https://api.rainviewer.com/public/"

    @Provides
    @Singleton
    fun providesNetworkApi(
        okHttpClient: OkHttpClient,
        @KtJson json: Json,
    ): RainViewerNetworkApi =
        Retrofit.Builder().client(okHttpClient).baseUrl(RAIN_VIEWER_URL).addCallAdapterFactory(NetworkApiCallAdapterFactory())
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            ).build().create(RainViewerNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesRainViewerDataSource(rainViewerNetworkApi: RainViewerNetworkApi): RainViewerDataSource {
        return RainViewerDataSourceImpl(rainViewerNetworkApi)
    }
}