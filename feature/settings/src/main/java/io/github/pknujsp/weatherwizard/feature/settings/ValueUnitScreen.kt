package io.github.pknujsp.weatherwizard.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.weatherwizard.core.ui.TextValueSettingItem
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.resource.R

@Composable
fun ValueUnitScreen(navController: NavController, viewModel: SettingsViewModel) {
    var bottomSheet by remember { mutableStateOf<BottomSheetInfo?>(null) }
    var displayValuesBottomSheet by remember { mutableStateOf(false) }

    Column {
        TitleTextWithNavigation(title = stringResource(id = R.string.title_value_unit)) {
            navController.popBackStack()
        }

        val temperature by viewModel.temperatureUnit.collectAsStateWithLifecycle()
        val windSpeed by viewModel.windSpeedUnit.collectAsStateWithLifecycle()
        val precipitation by viewModel.precipitationUnit.collectAsStateWithLifecycle()

        TextValueSettingItem(title = stringResource(id = R.string.title_temperature_unit), value = {
            temperature.symbol
        }) {
            bottomSheet =
                BottomSheetInfo(title = R.string.title_temperature_unit, units = TemperatureUnit.units, selectedUnit = temperature)
            displayValuesBottomSheet = true
        }

        TextValueSettingItem(title = stringResource(id = R.string.title_wind_speed_unit), value = {
            windSpeed.symbol
        }) {
            bottomSheet = BottomSheetInfo(title = R.string.title_wind_speed_unit, units = WindSpeedUnit.units, selectedUnit = windSpeed)
            displayValuesBottomSheet = true
        }

        TextValueSettingItem(title = stringResource(id = R.string.title_precipitation_unit), value = {
            precipitation.symbol
        }) {
            bottomSheet = BottomSheetInfo(title = R.string.title_precipitation_unit, units = PrecipitationUnit.units, selectedUnit =
            precipitation)
            displayValuesBottomSheet = true
        }
    }

    if (displayValuesBottomSheet) {
        bottomSheet?.run {
            ValuesBottomSheet(title = stringResource(id = title), units = units, selectedUnit = selectedUnit) {
                displayValuesBottomSheet = false
                it?.run {
                    viewModel.updateUnit(this)
                }
            }
        }
    }
}


private class BottomSheetInfo(
    val title: Int,
    val units: Array<out WeatherDataUnit>,
    val selectedUnit: WeatherDataUnit,
)