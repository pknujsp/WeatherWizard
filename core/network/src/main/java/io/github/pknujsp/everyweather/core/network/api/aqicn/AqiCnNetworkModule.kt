package io.github.pknujsp.everyweather.core.network.api.aqicn

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
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AqiCnNetworkModule {
    private const val URL = "https://api.waqi.info/"

    @Provides
    @Singleton
    fun providesNetworkApi(
        okHttpClient: OkHttpClient,
        @KtJson json: Json,
    ): AqiCnNetworkApi = Retrofit.Builder().client(okHttpClient).baseUrl(URL).addCallAdapterFactory(NetworkApiCallAdapterFactory())
        .addConverterFactory(
            ScalarsConverterFactory.create(),
        ).addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build().create(AqiCnNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesAqiCnDataSource(aqiCnNetworkApi: AqiCnNetworkApi): AqiCnDataSource = AqiCnDataSourceImpl(aqiCnNetworkApi)
}