package io.github.pknujsp.everyweather.feature.componentservice.widget.configure

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.model.ActionState
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.everyweather.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.LocationScreen
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation
import io.github.pknujsp.everyweather.core.ui.WeatherProvidersScreen
import io.github.pknujsp.everyweather.core.ui.box.CustomBox
import io.github.pknujsp.everyweather.core.ui.button.SecondaryButton
import io.github.pknujsp.everyweather.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.everyweather.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.everyweather.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.everyweather.feature.componentservice.RemoteViewsScreen
import io.github.pknujsp.everyweather.feature.permoptimize.feature.ShowAppSettingsActivity
import io.github.pknujsp.everyweather.feature.permoptimize.feature.SmallFeatureStateScreen
import io.github.pknujsp.everyweather.feature.permoptimize.feature.rememberAppFeatureState
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionState
import io.github.pknujsp.everyweather.feature.permoptimize.permission.rememberPermissionManager
import io.github.pknujsp.everyweather.feature.searchlocation.SearchLocationScreen
import kotlinx.coroutines.launch


@Composable
fun WidgetConfigureScreen(
    viewModel: WidgetConfigureViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val widget = viewModel.widget
    var showSearch by remember { mutableStateOf(false) }
    val actionState = viewModel.action

    val coroutineScope = rememberCoroutineScope()
    val batteryOptimizationFeatureState = rememberAppFeatureState(featureType = FeatureType.BatteryOptimization)
    val backgroundLocationPermissionManager = rememberPermissionManager(defaultPermissionType = FeatureType.Permission.BackgroundLocation)
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        when (actionState) {
            ConfigureActionState.SAVE_SUCCESS -> {
                createWidgetAndFinish(context as Activity, widget.widgetId)
            }

            ConfigureActionState.NO_LOCATION_IS_SELECTED -> {
                coroutineScope.launch {
                    showSnackbar(context, message = actionState.message, action = null, snackbarHostState)
                }
            }

            else -> {}
        }
    }

    if (showSearch) {
        SearchLocationScreen(onSelectedLocation = { newLocation ->
            newLocation?.let {
                widget.location = LocationTypeModel(locationType = LocationType.CustomLocation,
                    address = it.addressName,
                    latitude = it.latitude,
                    country = it.countryName,
                    longitude = it.longitude)
            }
            showSearch = false
        }, popBackStack = {
            showSearch = false
        })
    } else {
        CustomBox(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
            Column {
                TitleTextWithNavigation(title = stringResource(id = R.string.configure_widget)) {
                    (context as Activity).finish()
                }
                RemoteViewsScreen(RemoteViewsCreatorManager.getByWidgetType(widget.widgetType), viewModel.units)
                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LocationScreen(widget.location, onSelectedItem = {
                        widget.location = widget.location.copy(locationType = it)
                    }) {
                        showSearch = true
                    }
                    WeatherProvidersScreen(widget.weatherProvider) {
                        widget.weatherProvider = it
                    }
                    if (viewModel.refreshInterval != RefreshInterval.MANUAL) {
                        if (!batteryOptimizationFeatureState.isAvailable) {
                            SmallFeatureStateScreen(Modifier.padding(8.dp),
                                state = batteryOptimizationFeatureState.featureType,
                                onClickAction = {
                                    batteryOptimizationFeatureState.showSettingsActivity()
                                })
                        }
                        if (backgroundLocationPermissionManager.permissionState !is PermissionState.Granted) {
                            SmallFeatureStateScreen(Modifier.padding(8.dp),
                                state = FeatureType.Permission.BackgroundLocation,
                                onClickAction = {
                                    backgroundLocationPermissionManager.showSettingsActivity()
                                })
                        }
                    }
                }

                Box(modifier = Modifier.padding(12.dp)) {
                    SecondaryButton(text = stringResource(id = R.string.save), modifier = Modifier.fillMaxWidth()) {
                        if (viewModel.refreshInterval != RefreshInterval.MANUAL) {
                            if (!batteryOptimizationFeatureState.isAvailable) {
                                coroutineScope.launch {
                                    showSnackbar(context,
                                        batteryOptimizationFeatureState.featureType.message,
                                        batteryOptimizationFeatureState.featureType.action,
                                        snackbarHostState) {
                                        batteryOptimizationFeatureState.showSettingsActivity()
                                    }
                                }
                                return@SecondaryButton
                            }
                            if (backgroundLocationPermissionManager.permissionState !is PermissionState.Granted) {
                                coroutineScope.launch {
                                    showSnackbar(context,
                                        FeatureType.Permission.BackgroundLocation.message,
                                        FeatureType.Permission.BackgroundLocation.action,
                                        snackbarHostState) {
                                        backgroundLocationPermissionManager.showSettingsActivity()
                                    }
                                }
                                return@SecondaryButton
                            }
                        }
                        widget.save()
                    }
                }
            }
        }
    }

    if (batteryOptimizationFeatureState.isShowSettingsActivity) {
        ShowAppSettingsActivity(featureType = batteryOptimizationFeatureState.featureType) {
            batteryOptimizationFeatureState.hideSettingsActivity()
        }
    } else if (backgroundLocationPermissionManager.isShowSettingsActivity) {
        ShowAppSettingsActivity(featureType = FeatureType.Permission.BackgroundLocation) {
            backgroundLocationPermissionManager.hideSettingsActivity()
        }
    }

}


fun createWidgetAndFinish(activity: Activity, widgetId: Int) {
    val newWidgetIntent = ComponentPendingIntentManager.getIntent(activity.applicationContext,
        LoadWidgetDataArgument(LoadWidgetDataArgument.NEW_WIDGET, widgetId),
        AppComponentServiceReceiver.ACTION_REFRESH)
    activity.sendBroadcast(newWidgetIntent)

    val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
    activity.setResult(Activity.RESULT_OK, result)
    activity.finish()
}

private suspend fun showSnackbar(
    context: Context,
    @StringRes message: Int,
    @StringRes action: Int? = null,
    snackbarHostState: SnackbarHostState,
    onAction: (() -> Unit)? = null,
) {
    if (onAction == null && action == null) {
        snackbarHostState.showSnackbar(message = context.getString(message))
    } else {
        when (snackbarHostState.showSnackbar(message = context.getString(message),
            actionLabel = context.getString(action!!),
            duration = SnackbarDuration.Short)) {
            SnackbarResult.ActionPerformed -> {
                onAction!!.invoke()
            }

            SnackbarResult.Dismissed -> {
            }
        }
    }
}

enum class ConfigureActionState : ActionState {
    NO_LOCATION_IS_SELECTED {
        override val message: Int
            get() = R.string.no_location_is_selected
    },
    SAVE_SUCCESS {
        override val message: Int
            get() = R.string.added_widget
    },
}