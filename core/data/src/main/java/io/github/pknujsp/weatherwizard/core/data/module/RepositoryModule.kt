package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepository
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetAreaRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetAreaRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepository
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.database.AppDataStore
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDao
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDataSource
import io.github.pknujsp.weatherwizard.core.network.api.aqicn.AqiCnDataSource
import io.github.pknujsp.weatherwizard.core.network.api.nominatim.NominatimDataSource
import io.github.pknujsp.weatherwizard.core.network.api.rainviewer.RainViewerDataSource
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

    @Provides
    @Singleton
    fun providesRadartilesRepository(rainViewerDataSource: RainViewerDataSource): RadarTilesRepository =
        RadarTilesRepositoryImpl(rainViewerDataSource)

    @Provides
    @Singleton
    fun providesAirQualityRepository(aqiCnDataSource: AqiCnDataSource): AirQualityRepository =
        AirQualityRepositoryImpl(aqiCnDataSource)

    @Provides
    @Singleton
    fun providesFavoriteAreaRepository(favoriteAreaListDataSource: FavoriteAreaListDataSource): FavoriteAreaListRepository =
        FavoriteAreaListRepositoryImpl(favoriteAreaListDataSource)

    @Singleton
    @Provides
    fun providesTargetAreaRepository(appDataStore: AppDataStore) : TargetAreaRepository =
        TargetAreaRepositoryImpl(appDataStore)
}