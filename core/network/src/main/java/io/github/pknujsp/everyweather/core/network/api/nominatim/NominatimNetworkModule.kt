package io.github.pknujsp.everyweather.core.network.api.nominatim

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.everyweather.core.common.module.AppLocale
import io.github.pknujsp.everyweather.core.common.module.KtJson
import io.github.pknujsp.everyweather.core.network.retrofit.NetworkApiCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.Locale
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NominatimNetworkModule {
    private const val URL = "https://nominatim.openstreetmap.org/"

    @Provides
    @Singleton
    fun providesNominatimNetworkApi(
        okHttpClient: OkHttpClient,
        @KtJson json: Json,
    ): NominatimNetworkApi = Retrofit.Builder().client(okHttpClient).baseUrl(URL).addCallAdapterFactory(NetworkApiCallAdapterFactory())
        .addConverterFactory(
            ScalarsConverterFactory.create(),
        ).addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build().create(NominatimNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesNominatimDataSource(
        nominatimNetworkApi: NominatimNetworkApi,
        @AppLocale appLocale: Locale,
    ): NominatimDataSource = NominatimDataSourceImpl(nominatimNetworkApi, appLocale)
}