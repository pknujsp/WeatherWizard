package io.github.pknujsp.everyweather.feature.settings

import android.content.Context
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.everyweather.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.BottomSheetSettingItem
import io.github.pknujsp.everyweather.core.ui.ButtonSettingItem
import io.github.pknujsp.everyweather.core.ui.CheckBoxSettingItem
import io.github.pknujsp.everyweather.core.ui.ClickableSettingItem
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation
import io.github.pknujsp.everyweather.feature.componentservice.manager.AppComponentServiceManagerFactory
import io.github.pknujsp.everyweather.feature.permoptimize.feature.ShowSettingsActivity
import io.github.pknujsp.everyweather.feature.permoptimize.feature.SmallFeatureStateScreen
import io.github.pknujsp.everyweather.feature.permoptimize.feature.rememberFeatureStateManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.rememberPermissionStateManager
import kotlinx.coroutines.launch

private const val PRIVACY_POLICY_URL = "https://www.notion.so/everyweatherapp/0d146d8e00934c39b5effd671dada8db"
private const val APP_UPDATE_PREVIEW_URL = "https://www.notion.so/everyweatherapp/72eb45a530f540d6a91c2fe286c51c0d"

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val settingsUiState = viewModel.mainSettingsUiState
    val coroutineScope = rememberCoroutineScope()

    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val backDispatcher = remember(onBackPressedDispatcherOwner) {
        onBackPressedDispatcherOwner?.onBackPressedDispatcher
    }

    val batteryOptimizationFeatureState = rememberFeatureStateManager(featureType = FeatureType.BatteryOptimization)
    val backgroundLocationPermissionManager = rememberPermissionStateManager(permissionType = FeatureType.Permission.BackgroundLocation)

    Column {
        TitleTextWithNavigation(title = stringResource(id = R.string.nav_settings), onClickNavigation = {
            backDispatcher?.onBackPressed()
        })
        ButtonSettingItem(
            title = stringResource(id = R.string.title_value_unit),
            description = stringResource(id = R.string.description_value_unit),
            onClick = {
                navController.navigate(SettingsRoutes.ValueUnit.route)
            },
        ) {
            Icon(painterResource(id = R.drawable.ic_forward), contentDescription = null)
        }

        BottomSheetSettingItem(
            title = stringResource(id = R.string.title_weather_data_provider),
            selectedItem = settingsUiState.weatherProvider,
            onSelectedItem = {
                it?.run {
                    settingsUiState.updatePreference(WeatherProvider, this)
                }
            },
            enums = WeatherProvider.enums,
        )
        CheckBoxSettingItem(
            title = stringResource(id = R.string.title_weather_condition_animation),
            description = stringResource(id = R.string.description_weather_condition_animation),
            checked = true,
        )
        BottomSheetSettingItem(
            title = stringResource(id = R.string.title_widget_auto_refresh_interval),
            selectedItem = settingsUiState.widgetAutoRefreshInterval,
            onSelectedItem = {
                it?.let { refreshInterval ->
                    settingsUiState.updatePreference(RefreshInterval, refreshInterval)
                    coroutineScope.launch {
                        rescheduleWidgetAutoRefresh(context, refreshInterval)
                    }
                }
            },
            enums = RefreshInterval.enums,
        )
        ClickableSettingItem(
            title = stringResource(id = R.string.title_refresh_widget),
            description = stringResource(id = R.string.description_refresh_widget),
        ) {
            coroutineScope.launch {
                redrawAppWidgets(context)
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
        ClickableSettingItem(title = stringResource(id = R.string.title_privacy_policy), description = null, onClick = {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(context, PRIVACY_POLICY_URL.toUri())
        })
        ClickableSettingItem(title = stringResource(id = R.string.title_app_update_preview), description = null, onClick = {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(context, APP_UPDATE_PREVIEW_URL.toUri())
        })

        if (settingsUiState.widgetAutoRefreshInterval != RefreshInterval.MANUAL) {
            if (!batteryOptimizationFeatureState.isEnabled(context)) {
                SmallFeatureStateScreen(Modifier.padding(8.dp), state = batteryOptimizationFeatureState.featureType, onClickAction = {
                    batteryOptimizationFeatureState.showSettingsActivity()
                })
            }
            if (!backgroundLocationPermissionManager.isEnabled(context)) {
                SmallFeatureStateScreen(Modifier.padding(8.dp), state = backgroundLocationPermissionManager.featureType, onClickAction = {
                    backgroundLocationPermissionManager.showSettingsActivity()
                })
            }

            if (batteryOptimizationFeatureState.isShowSettingsActivity) {
                ShowSettingsActivity(featureType = batteryOptimizationFeatureState.featureType) {
                    batteryOptimizationFeatureState.hideSettingsActivity()
                }
            }
            if (backgroundLocationPermissionManager.isShowSettingsActivity) {
                ShowSettingsActivity(featureType = backgroundLocationPermissionManager.featureType) {
                    backgroundLocationPermissionManager.hideSettingsActivity()
                    backgroundLocationPermissionManager.requestPermission()
                }
            }
        }
    }
}

@Composable
fun HostSettingsScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        route = SettingsRoutes.route,
        startDestination = SettingsRoutes.Main.route,
        modifier = Modifier.systemBarsPadding(),
    ) {
        composable(SettingsRoutes.Main.route) { SettingsScreen(navController) }
        composable(SettingsRoutes.ValueUnit.route) { ValueUnitScreen(navController) }
    }
}

private fun rescheduleWidgetAutoRefresh(
    context: Context,
    refreshInterval: RefreshInterval,
) {
    AppComponentServiceManagerFactory.getManager(context, AppComponentServiceManagerFactory.WIDGET_ALARM_MANAGER).run {
        if (refreshInterval == RefreshInterval.MANUAL) {
            unScheduleAutoRefresh()
        } else {
            scheduleAutoRefresh(refreshInterval)
        }
    }
}

private fun redrawAppWidgets(context: Context) {
    AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.WIDGET_MANAGER).redrawAllWidgets()
}