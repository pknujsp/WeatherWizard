package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManagerImpl
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestPreProcessorManager
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDataSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiRequestManagerModule {

    @Provides
    @Singleton
    fun providesWeatherApiRequestManager(
        kmaDataSource: KmaDataSource,
        weatherApiRequestPreProcessorManager: WeatherApiRequestPreProcessorManager
    ): WeatherApiRequestManager = WeatherApiRequestManagerImpl(kmaDataSource, weatherApiRequestPreProcessorManager)

}