package io.github.pknujsp.weatherwizard.feature.settings

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.ButtonSettingItem
import io.github.pknujsp.weatherwizard.core.ui.CheckBoxSettingItem
import io.github.pknujsp.weatherwizard.core.ui.ClickableSettingItem
import io.github.pknujsp.weatherwizard.core.ui.TextValueSettingItem

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val weatherDataProvider by viewModel.weatherProvider.collectAsStateWithLifecycle()
    var displayValuesBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column {
        ButtonSettingItem(title = stringResource(id = R.string.title_value_unit),
            description = stringResource(id = R.string.description_value_unit),
            onClick = {
                navController.navigate(SettingsRoutes.ValueUnit.route)
            }) {
            Icon(painterResource(id = R.drawable.ic_forward), contentDescription = "navigate")
        }

        TextValueSettingItem(title = stringResource(id = R.string.title_weather_data_provider), value = {
            context.getString(weatherDataProvider.name)
        }) {
            displayValuesBottomSheet = true
        }
        CheckBoxSettingItem(title = stringResource(id = R.string.title_weather_condition_animation),
            description = stringResource(id = R.string.description_weather_condition_animation),
            checked = true) {

        }

        ClickableSettingItem(title = stringResource(id = R.string.title_refresh_widget),
            description = stringResource(id = R.string.description_refresh_widget)) {
            viewModel.refreshWidgets(context)
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
    val window = (LocalContext.current as Activity).window
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = true

    NavHost(navController = navController,
        route = SettingsRoutes.route,
        startDestination = SettingsRoutes.Main.route,
        modifier = Modifier.navigationBarsPadding()) {
        composable(SettingsRoutes.Main.route) { SettingsScreen(navController, viewModel) }
        composable(SettingsRoutes.ValueUnit.route) { ValueUnitScreen(navController, viewModel) }
    }
}