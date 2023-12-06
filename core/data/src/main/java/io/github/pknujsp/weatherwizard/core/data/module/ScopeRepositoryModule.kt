package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializer
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializerManager
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializerManagerImpl
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepository
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepositoryImpl
import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryLocalDataSource
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScopeRepositoryModule {

    private const val WEATHER_REPOSITORY = "WEATHER_REPOSITORY"
    private const val AIR_QUALITY_REPOSITORY = "AIR_QUALITY_REPOSITORY"

    @Provides
    fun providesSearchHistoryRepository(searchHistoryLocalDataSource: SearchHistoryLocalDataSource): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(searchHistoryLocalDataSource)


    @Provides
    @Named(WEATHER_REPOSITORY)
    fun providesWeatherRepositoryInitializer(weatherDataRepositoryImpl: WeatherDataRepositoryImpl): RepositoryInitializer =
        weatherDataRepositoryImpl

    @Provides
    @Named(AIR_QUALITY_REPOSITORY)
    fun providesAirQualityRepositoryInitializer(airQualityRepositoryImpl: AirQualityRepositoryImpl): RepositoryInitializer =
        airQualityRepositoryImpl

    @Provides
    @Singleton
    fun providesRepositoryInitializer(
        @Named(WEATHER_REPOSITORY) weatherRepository: RepositoryInitializer,
        @Named(AIR_QUALITY_REPOSITORY) airQualityRepository: RepositoryInitializer
    ): RepositoryInitializerManager = RepositoryInitializerManagerImpl(weatherRepository, airQualityRepository)

}