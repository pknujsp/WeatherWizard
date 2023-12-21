package io.github.pknujsp.weatherwizard.core.network


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()
}