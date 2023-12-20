package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.weather.kma.KmaResponseMapper
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapper
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManagerImpl
import io.github.pknujsp.weatherwizard.core.data.weather.metnorway.MetNorwayResponseMapper
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaHourlyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaYesterdayWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayHourlyForecastResponse
import javax.inject.Named
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