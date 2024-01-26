package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManagerImpl
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestPreProcessorManager
import io.github.pknujsp.weatherwizard.core.model.ApiRequestParameter
import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaDataSource
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.MetNorwayDataSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiRequestManagerModule {

    @Provides
    @Singleton
    fun providesWeatherApiRequestManager(
        kmaDataSource: KmaDataSource,
        metNorwayDataSource: MetNorwayDataSource,
        weatherApiRequestPreProcessorManager: WeatherApiRequestPreProcessorManager<@JvmSuppressWildcards ApiRequestParameter>,
    ): WeatherApiRequestManager<ApiResponseModel> =
        WeatherApiRequestManagerImpl(kmaDataSource, metNorwayDataSource, weatherApiRequestPreProcessorManager)

}