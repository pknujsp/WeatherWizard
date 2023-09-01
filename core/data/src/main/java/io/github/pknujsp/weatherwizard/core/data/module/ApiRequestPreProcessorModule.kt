package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherRequestPreProcessor
import io.github.pknujsp.weatherwizard.core.data.weather.kma.KmaRequestPreProcessor
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestPreProcessorManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestPreProcessorManagerImpl
import io.github.pknujsp.weatherwizard.core.database.coordinate.KorCoordinateDao
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiRequestPreProcessorModule {

    private const val KMA_PRE_PROCESSOR = "KmaPreProcessor"

    @Provides
    @Singleton
    @Named(KMA_PRE_PROCESSOR)
    fun providesKmaPreProcessor(korCoordinateDao: KorCoordinateDao): WeatherRequestPreProcessor = KmaRequestPreProcessor(korCoordinateDao)

    @Singleton
    @Provides
    fun providesRequestPreProcessorManager(
        @Named(KMA_PRE_PROCESSOR) kmaPreProcessor: WeatherRequestPreProcessor
    ): WeatherApiRequestPreProcessorManager = WeatherApiRequestPreProcessorManagerImpl(kmaPreProcessor)
}