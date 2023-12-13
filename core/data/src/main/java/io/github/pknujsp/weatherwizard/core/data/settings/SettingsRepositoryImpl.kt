package io.github.pknujsp.weatherwizard.core.data.settings

import io.github.pknujsp.weatherwizard.core.database.AppDataStore
import io.github.pknujsp.weatherwizard.core.model.DBEntityState
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SettingsRepositoryImpl(
    private val appDataStore: AppDataStore
) : SettingsRepository {

    private var _currentUnits = MutableStateFlow(CurrentUnits())

    override val currentUnits: StateFlow<CurrentUnits> = _currentUnits

    override suspend fun init() {
        _currentUnits.value = CurrentUnits(temperatureUnit = getTemperatureUnit(),
            windSpeedUnit = getWindSpeedUnit(),
            precipitationUnit = getPrecipitationUnit())
    }

    override suspend fun getTemperatureUnit(): TemperatureUnit {
        return when (val unit = appDataStore.readAsString(TemperatureUnit.key)) {
            is DBEntityState.Exists -> TemperatureUnit.getUnit(unit.data)
            is DBEntityState.NotExists -> TemperatureUnit.Celsius
        }.apply {
            _currentUnits.update {
                it.copy(temperatureUnit = this)
            }
        }
    }

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        appDataStore.save(TemperatureUnit.key, unit.symbol)
        _currentUnits.update {
            it.copy(temperatureUnit = unit)
        }
    }

    override suspend fun getWindSpeedUnit(): WindSpeedUnit {
        return when (val unit = appDataStore.readAsString(WindSpeedUnit.key)) {
            is DBEntityState.Exists -> WindSpeedUnit.getUnit(unit.data)
            is DBEntityState.NotExists -> WindSpeedUnit.KilometerPerHour
        }.apply {
            _currentUnits.update {
                it.copy(windSpeedUnit = this)
            }
        }
    }

    override suspend fun setWindSpeedUnit(unit: WindSpeedUnit) {
        appDataStore.save(WindSpeedUnit.key, unit.symbol)
        _currentUnits.update {
            it.copy(windSpeedUnit = unit)
        }
    }

    override suspend fun getPrecipitationUnit(): PrecipitationUnit {
        return when (val unit = appDataStore.readAsString(PrecipitationUnit.key)) {
            is DBEntityState.Exists -> PrecipitationUnit.getUnit(unit.data)
            is DBEntityState.NotExists -> PrecipitationUnit.Millimeter
        }.apply {
            _currentUnits.update {
                it.copy(precipitationUnit = this)
            }
        }
    }

    override suspend fun setPrecipitationUnit(unit: PrecipitationUnit) {
        appDataStore.save(PrecipitationUnit.key, unit.symbol)
        _currentUnits.update {
            it.copy(precipitationUnit = unit)
        }
    }

    override suspend fun getWeatherDataProvider(): WeatherProvider {
        return when (val provider = appDataStore.readAsLong(WeatherProvider.key)) {
            is DBEntityState.Exists -> WeatherProvider.fromKey(provider.data.toInt())
            is DBEntityState.NotExists -> WeatherProvider.default
        }
    }

    override suspend fun setWeatherDataProvider(provider: WeatherProvider) {
        appDataStore.save(WeatherProvider.key, provider.key.toLong())
    }
}