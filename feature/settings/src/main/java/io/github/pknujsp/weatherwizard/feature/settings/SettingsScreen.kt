package io.github.pknujsp.weatherwizard.feature.settings

import android.content.Context
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.BottomSheetSettingItem
import io.github.pknujsp.weatherwizard.core.ui.ButtonSettingItem
import io.github.pknujsp.weatherwizard.core.ui.CheckBoxSettingItem
import io.github.pknujsp.weatherwizard.core.ui.ClickableSettingItem
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val settingsUiState = viewModel.mainSettingsUiState

    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val backDispatcher = remember {
        onBackPressedDispatcherOwner?.onBackPressedDispatcher
    }

    Column {
        TitleTextWithNavigation(title = stringResource(id = R.string.nav_settings), onClickNavigation = {
            backDispatcher?.onBackPressed()
        })
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
                    rescheduleWidgetAutoRefresh(this, context, viewModel.appAlarmManager, viewModel.widgetManager)
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
        modifier = Modifier.systemBarsPadding()) {
        composable(SettingsRoutes.Main.route) { SettingsScreen(navController) }
        composable(SettingsRoutes.ValueUnit.route) { ValueUnitScreen(navController) }
    }
}

private fun rescheduleWidgetAutoRefresh(
    refreshInterval: RefreshInterval, context: Context, appAlarmManager: AppAlarmManager, widgetManager: WidgetManager
) {
    val scheduler = AppWidgetAutoRefreshScheduler(widgetManager)
    scheduler.scheduleAutoRefresh(context, appAlarmManager, refreshInterval)
}