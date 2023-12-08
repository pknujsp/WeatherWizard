package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializer
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializerManager
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializerManagerImpl
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepository
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepositoryImpl
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
    private const val RAIN_VIEWER_REPOSITORY = "RAIN_VIEWER_REPOSITORY"

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
    @Named(RAIN_VIEWER_REPOSITORY)
    fun providesRainViewerRepositoryInitializer(radarTilesRepositoryImpl: RadarTilesRepositoryImpl): RepositoryInitializer =
        radarTilesRepositoryImpl

    @Provides
    @Singleton
    fun providesRepositoryInitializer(
        @Named(WEATHER_REPOSITORY) weatherRepository: RepositoryInitializer,
        @Named(AIR_QUALITY_REPOSITORY) airQualityRepository: RepositoryInitializer,
        @Named(RAIN_VIEWER_REPOSITORY) rainViewerRepository: RepositoryInitializer
    ): RepositoryInitializerManager = RepositoryInitializerManagerImpl(weatherRepository, airQualityRepository, rainViewerRepository)

}