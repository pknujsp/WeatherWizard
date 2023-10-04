package io.github.pknujsp.weatherwizard.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithoutNavigation

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val weatherDataProvider by viewModel.weatherDataProvider.collectAsStateWithLifecycle()
    var displayValuesBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column {
        TitleTextWithoutNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.nav_settings))
        ButtonSettingItem(title = stringResource(id = R.string.title_value_unit),
            description = stringResource(id = R.string.description_value_unit)) {
            navController.navigate(SettingsRoutes.ValueUnit.route)
        }

        TextValueSettingItem(title = stringResource(id = R.string.title_weather_data_provider), value = {
            context.getString(weatherDataProvider.name)
        }) {
            displayValuesBottomSheet = true
        }
        CheckBoxSettingItem(title = stringResource(id = R.string.title_weather_condition_animation), description =
        stringResource(id = R.string.description_weather_condition_animation), checked = true) {

        }
    }

    if (displayValuesBottomSheet) {
        WeatherDataProviderBottomSheet(weatherDataProvider) {
            displayValuesBottomSheet = false
            it?.run {
                viewModel.updateWeatherDataProvider(this)
            }
        }
    }
}


@Composable
fun HostSettingsScreen() {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<SettingsViewModel>()

    NavHost(navController = navController, route = SettingsRoutes.route, startDestination = SettingsRoutes.Main.route,
        modifier = Modifier.navigationBarsPadding()) {
        composable(SettingsRoutes.Main.route) { SettingsScreen(navController, viewModel) }
        composable(SettingsRoutes.ValueUnit.route) { ValueUnitScreen(navController, viewModel) }
    }
}