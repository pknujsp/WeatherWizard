package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepository
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetLocationRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepository
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManagerImpl
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepositoryImpl
import io.github.pknujsp.weatherwizard.core.database.AppDataStore
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDataSource
import io.github.pknujsp.weatherwizard.core.database.notification.daily.DailyNotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.database.notification.ongoing.OngoingNotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetLocalDataSource
import io.github.pknujsp.weatherwizard.core.network.api.aqicn.AqiCnDataSource
import io.github.pknujsp.weatherwizard.core.network.api.nominatim.NominatimDataSource
import io.github.pknujsp.weatherwizard.core.network.api.rainviewer.RainViewerDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import java.time.Duration
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun providesWeatherRepositoryImpl(
        weatherResponseMapperManager: WeatherResponseMapperManager,
        weatherApiRequestManager: WeatherApiRequestManager,
        @CoDispatcher(CoDispatcherType.DEFAULT) dispatcher: CoroutineDispatcher
    ): WeatherDataRepositoryImpl =
        WeatherDataRepositoryImpl(weatherResponseMapperManager, weatherApiRequestManager, CacheManagerImpl(dispatcher = dispatcher))

    @Provides
    fun providesWeatherRepository(
        weatherDataRepositoryImpl: WeatherDataRepositoryImpl
    ): WeatherDataRepository = weatherDataRepositoryImpl


    @Provides
    @Singleton
    fun providesNominatimRepository(nominatimDataSource: NominatimDataSource): NominatimRepository =
        NominatimRepositoryImpl(nominatimDataSource)

    @Provides
    @Singleton
    fun providesRadartilesRepositoryImpl(
        rainViewerDataSource: RainViewerDataSource, @CoDispatcher(CoDispatcherType.DEFAULT) dispatcher: CoroutineDispatcher
    ): RadarTilesRepositoryImpl = RadarTilesRepositoryImpl(rainViewerDataSource, CacheManagerImpl(dispatcher = dispatcher))

    @Provides
    fun providesRadartilesRepository(radarTilesRepositoryImpl: RadarTilesRepositoryImpl): RadarTilesRepository = radarTilesRepositoryImpl

    @Provides
    @Singleton
    fun providesAirQualityRepositoryImpl(
        aqiCnDataSource: AqiCnDataSource, @CoDispatcher(CoDispatcherType.DEFAULT) dispatcher: CoroutineDispatcher
    ): AirQualityRepositoryImpl = AirQualityRepositoryImpl(aqiCnDataSource,
        CacheManagerImpl(cacheExpiryTime = Duration.ofMinutes(10),
            cleaningInterval = Duration.ofMinutes(15),
            dispatcher = dispatcher))

    @Provides
    fun providesAirQualityRepository(
        airQualityRepositoryImp: AirQualityRepositoryImpl
    ): AirQualityRepository = airQualityRepositoryImp


    @Provides
    @Singleton
    fun providesFavoriteAreaRepository(favoriteAreaListDataSource: FavoriteAreaListDataSource): FavoriteAreaListRepository =
        FavoriteAreaListRepositoryImpl(favoriteAreaListDataSource)

    @Singleton
    @Provides
    fun providesTargetAreaRepository(appDataStore: AppDataStore): TargetLocationRepository = TargetLocationRepositoryImpl(appDataStore)

    @Singleton
    @Provides
    fun providesSettingsRepository(appDataStore: AppDataStore): SettingsRepository = SettingsRepositoryImpl(appDataStore)

    @Singleton
    @Provides
    fun providesDailyNotificationRepository(
        dataSource: DailyNotificationLocalDataSource, @KtJson json: Json
    ): DailyNotificationRepository = DailyNotificationRepositoryImpl(dataSource, json)

    @Singleton
    @Provides
    fun providesOngoingNotificationRepository(
        dataSource: OngoingNotificationLocalDataSource, @KtJson json: Json
    ): OngoingNotificationRepository = OngoingNotificationRepositoryImpl(dataSource, json)

    @Singleton
    @Provides
    fun providesWidgetRepository(
        widgetLocalDataSource: WidgetLocalDataSource, @KtJson json: Json
    ): WidgetRepository = WidgetRepositoryImpl(widgetLocalDataSource, json)
}