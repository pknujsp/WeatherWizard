package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.weather.kma.KmaRequestPreProcessor
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestPreProcessorManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestPreProcessorManagerImpl
import io.github.pknujsp.weatherwizard.core.database.kma.KmaAreaCodesDao
import io.github.pknujsp.weatherwizard.core.model.ApiRequestParameter
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiRequestPreProcessorModule {

    private const val KMA_PRE_PROCESSOR = "KmaPreProcessor"

    @Provides
    fun providesKmaPreProcessor(kmaAreaCodesDao: KmaAreaCodesDao): KmaRequestPreProcessor = KmaRequestPreProcessor(kmaAreaCodesDao)

    @Singleton
    @Provides
    fun providesRequestPreProcessorManager(
        kmaPreProcessor: KmaRequestPreProcessor
    ): WeatherApiRequestPreProcessorManager<ApiRequestParameter> = WeatherApiRequestPreProcessorManagerImpl(kmaPreProcessor)
}