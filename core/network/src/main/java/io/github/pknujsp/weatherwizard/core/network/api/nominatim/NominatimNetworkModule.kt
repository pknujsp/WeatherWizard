package io.github.pknujsp.weatherwizard.core.network.api.nominatim

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.common.module.AppLocale
import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.network.datasource.nominatim.NominatimDataSource
import io.github.pknujsp.weatherwizard.core.network.datasource.nominatim.NominatimDataSourceImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.Locale
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NominatimNetworkModule {
    private const val nominatimUrl = "https://nominatim.openstreetmap.org/"

    @Provides
    @Singleton
    fun providesNominatimNetworkApi(okHttpClient: OkHttpClient, @KtJson json: Json): NominatimNetworkApi =
        Retrofit.Builder().client(okHttpClient).baseUrl(nominatimUrl).addConverterFactory(
            json.asConverterFactory(MediaType.get("application/json"))
        ).build().create(NominatimNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesNominatimDataSource(nominatimNetworkApi: NominatimNetworkApi, @AppLocale appLocale: Locale): NominatimDataSource =
        NominatimDataSourceImpl(nominatimNetworkApi, appLocale)
}