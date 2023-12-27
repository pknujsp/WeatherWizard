package io.github.pknujsp.weatherwizard.feature.settings

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.BottomSheetSettingItem
import io.github.pknujsp.weatherwizard.core.ui.ButtonSettingItem
import io.github.pknujsp.weatherwizard.core.ui.CheckBoxSettingItem
import io.github.pknujsp.weatherwizard.core.ui.ClickableSettingItem

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val settingsUiState = viewModel.mainSettingsUiState

    Column {
        ButtonSettingItem(title = stringResource(id = R.string.title_value_unit),
            description = stringResource(id = R.string.description_value_unit),
            onClick = {
                navController.navigate(SettingsRoutes.ValueUnit.route)
            }) {
            Icon(painterResource(id = R.drawable.ic_forward), contentDescription = null)
        }

        BottomSheetSettingItem(title = stringResource(id = R.string.title_weather_data_provider),
            selectedItem = settingsUiState.weatherProvider,
            onSelectedItem = {
                it?.run {
                    settingsUiState.updatePreference(WeatherProvider, this)
                }
            },
            enums = WeatherProvider.enums)
        CheckBoxSettingItem(title = stringResource(id = R.string.title_weather_condition_animation),
            description = stringResource(id = R.string.description_weather_condition_animation),
            checked = true) {

        }
        BottomSheetSettingItem(title = stringResource(id = R.string.title_widget_auto_refresh_interval),
            selectedItem = settingsUiState.widgetAutoRefreshInterval,
            onSelectedItem = {
                it?.run {
                    settingsUiState.updatePreference(RefreshInterval, this)
                }
            },
            enums = RefreshInterval.enums)
        ClickableSettingItem(title = stringResource(id = R.string.title_refresh_widget),
            description = stringResource(id = R.string.description_refresh_widget)) {
            viewModel.reDrawAppWidgets(context)
        }
    }
}


@Composable
fun HostSettingsScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController,
        route = SettingsRoutes.route,
        startDestination = SettingsRoutes.Main.route,
        modifier = Modifier) {
        composable(SettingsRoutes.Main.route) { SettingsScreen(navController) }
        composable(SettingsRoutes.ValueUnit.route) { ValueUnitScreen(navController) }
    }
}