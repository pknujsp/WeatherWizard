package io.github.pknujsp.weatherwizard.core.network.api.kma

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDataSource
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDataSourceImpl
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser.KmaHtmlParser
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
        Retrofit.Builder().client(okHttpClient).baseUrl(KMA_URL).addConverterFactory(
            ScalarsConverterFactory.create(),
        ).build().create(KmaNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesKmaDataSource(
        kmaNetworkApi: KmaNetworkApi,
        kmaHtmlParser: KmaHtmlParser,
    ): KmaDataSource = KmaDataSourceImpl(kmaNetworkApi, kmaHtmlParser)

}