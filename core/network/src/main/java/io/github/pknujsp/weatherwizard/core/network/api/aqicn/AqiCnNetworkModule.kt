package io.github.pknujsp.weatherwizard.core.network.api.aqicn

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
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AqiCnNetworkModule {
    private const val URL = "https://api.waqi.info/"

    @Provides
    @Singleton
    fun providesNetworkApi(okHttpClient: OkHttpClient, @KtJson json: Json): AqiCnNetworkApi =
        Retrofit.Builder().client(okHttpClient).baseUrl(URL)
            .addCallAdapterFactory(NetworkApiCallAdapterFactory())
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            ).build().create(AqiCnNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesAqiCnDataSource(aqiCnNetworkApi: AqiCnNetworkApi): AqiCnDataSource =
        AqiCnDataSourceImpl(aqiCnNetworkApi)
}