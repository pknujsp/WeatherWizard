package io.github.pknujsp.weatherwizard.core.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.weather.kma.KmaResponseMapper
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapper
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiResponseMapperModule {

    @Provides
    @Singleton
    @Named("KmaResponseMapper")
    fun providesKmaResponseMapper(): WeatherResponseMapper<*, *, *, *> = KmaResponseMapper()
}