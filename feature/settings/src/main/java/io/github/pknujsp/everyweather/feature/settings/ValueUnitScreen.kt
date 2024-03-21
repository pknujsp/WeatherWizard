package io.github.pknujsp.everyweather.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherDataUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.BottomSheetSettingItem
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation

@Composable
fun ValueUnitScreen(
    navController: NavController,
    viewModel: UnitSettingsViewModel = hiltViewModel(),
) {
    val settingsUiState = viewModel.unitSettingsUiState

    Column {
        TitleTextWithNavigation(title = stringResource(id = R.string.title_value_unit)) {
            navController.popBackStack()
        }
        BottomSheetSettingItem(
            title = stringResource(id = R.string.title_temperature_unit),
            selectedItem = settingsUiState.temperatureUnit,
            onSelectedItem = {
                it?.run {
                    settingsUiState.updatePreference(TemperatureUnit, this)
                }
            },
            enums = TemperatureUnit.enums,
        )
        BottomSheetSettingItem(
            title = stringResource(id = R.string.title_precipitation_unit),
            selectedItem = settingsUiState.precipitationUnit,
            onSelectedItem = {
                it?.run {
                    settingsUiState.updatePreference(PrecipitationUnit, this)
                }
            },
            enums = PrecipitationUnit.enums,
        )
        BottomSheetSettingItem(
            title = stringResource(id = R.string.title_wind_speed_unit),
            selectedItem = settingsUiState.windSpeedUnit,
            onSelectedItem = {
                it?.run {
                    settingsUiState.updatePreference(WindSpeedUnit, this)
                }
            },
            enums = WindSpeedUnit.enums,
        )
    }
}

private class BottomSheetInfo(
    val title: Int,
    val units: Array<out WeatherDataUnit>,
    val selectedUnit: WeatherDataUnit,
)
