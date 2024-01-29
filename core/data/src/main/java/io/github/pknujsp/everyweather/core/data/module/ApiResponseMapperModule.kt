package io.github.pknujsp.everyweather.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.everyweather.core.data.weather.kma.KmaResponseMapper
import io.github.pknujsp.everyweather.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.everyweather.core.data.weather.mapper.WeatherResponseMapperManagerImpl
import io.github.pknujsp.everyweather.core.data.weather.metnorway.MetNorwayResponseMapper
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiResponseMapperModule {

    private const val KMA_WEATHER_RESPONSE_MAPPER = "KmaWeatherResponseMapper"
    private const val METNORWAY_WEATHER_RESPONSE_MAPPER = "MetNorwayWeatherResponseMapper"

    @Singleton
    @Provides
    fun providesWeatherResponseMapperManager(
    ): WeatherResponseMapperManager<WeatherEntityModel> = WeatherResponseMapperManagerImpl(KmaResponseMapper(), MetNorwayResponseMapper())
}