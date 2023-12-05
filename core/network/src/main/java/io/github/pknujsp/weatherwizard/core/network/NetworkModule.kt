package io.github.pknujsp.weatherwizard.core.network


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()
}