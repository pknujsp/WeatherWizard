package io.github.pknujsp.everyweather.core.network.api.kma

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.everyweather.core.network.api.kma.parser.KmaHtmlParser
import io.github.pknujsp.everyweather.core.network.retrofit.NetworkApiCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object KmaNetworkModule {
    private const val KMA_URL = "https://www.weather.go.kr/w/wnuri-fct2021/main/"

    @Provides
    @Singleton
    fun providesKmaNetworkApi(okHttpClient: OkHttpClient): KmaNetworkApi =
        Retrofit.Builder().client(okHttpClient).baseUrl(KMA_URL).addCallAdapterFactory(NetworkApiCallAdapterFactory()).addConverterFactory(
            ScalarsConverterFactory.create(),
        ).build().create(KmaNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesKmaDataSource(kmaNetworkApi: KmaNetworkApi): KmaDataSource = KmaDataSourceImpl(kmaNetworkApi, KmaHtmlParser())
}
