package io.github.pknujsp.weatherwizard.core.data.settings

import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val currentUnits: StateFlow<CurrentUnits>

    suspend fun init()

    suspend fun getTemperatureUnit(): TemperatureUnit
    suspend fun setTemperatureUnit(unit: TemperatureUnit)

    suspend fun getWindSpeedUnit(): WindSpeedUnit

    suspend fun setWindSpeedUnit(unit: WindSpeedUnit)

    suspend fun getPrecipitationUnit(): PrecipitationUnit

    suspend fun setPrecipitationUnit(unit: PrecipitationUnit)

    suspend fun getWeatherDataProvider(): WeatherProvider

    suspend fun setWeatherDataProvider(provider: WeatherProvider)
}