package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.network.datasource.nominatim.NominatimDataSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun providesWeatherRepository(
        weatherResponseMapperManager: WeatherResponseMapperManager,
        weatherApiRequestManager: WeatherApiRequestManager
    ): WeatherDataRepository =
        WeatherDataRepositoryImpl(weatherResponseMapperManager, weatherApiRequestManager)


    @Provides
    @Singleton
    fun providesNominatimRepository(nominatimDataSource: NominatimDataSource): NominatimRepository =
        NominatimRepositoryImpl(nominatimDataSource)
}