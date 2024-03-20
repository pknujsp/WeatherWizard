package io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.everyweather.core.model.notification.enums.OngoingNotificationType
import io.github.pknujsp.everyweather.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.BottomSheetSettingItem
import io.github.pknujsp.everyweather.core.ui.LocationScreen
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation
import io.github.pknujsp.everyweather.core.ui.WeatherProvidersScreen
import io.github.pknujsp.everyweather.core.ui.box.CustomBox
import io.github.pknujsp.everyweather.core.ui.button.SecondaryButton
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.everyweather.feature.componentservice.RemoteViewsScreen
import io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.model.OngoingNotificationSettings
import io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.model.rememberOngoingNotificationState
import io.github.pknujsp.everyweather.feature.permoptimize.feature.SmallFeatureStateScreen
import io.github.pknujsp.everyweather.feature.searchlocation.SearchLocationScreen


@Composable
fun OngoingNotificationScreen(navController: NavController, viewModel: OngoingNotificationViewModel = hiltViewModel()) {
    val notificationState = rememberOngoingNotificationState(navController, viewModel.ongoingNotificationUiState)
    notificationState.run {
        if (showSearch) {
            SearchLocationScreen(onSelectedLocation = { newLocation ->
                newLocation?.let {
                    notificationUiState.settings.location = notificationUiState.settings.location.copy(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        address = it.addressName,
                        country = it.countryName,
                    )
                }
                showSearch = false
            }, popBackStack = {
                showSearch = false
            })
        } else {
            CustomBox(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
                Column {
                    TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.title_ongoing_notification)) {
                        navController.popBackStack()
                    }
                    RemoteViewsScreen(RemoteViewsCreatorManager.getByOngoingNotificationType(OngoingNotificationType.CURRENT_HOURLY_FORECAST),
                        viewModel.units,
                        modifier = Modifier.padding(12.dp))
                    Column(modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = stringResource(id = R.string.switch_ongoing_notification), modifier = Modifier.weight(1f))
                            Switch(
                                checked = notificationUiState.isEnabled,
                                onCheckedChange = {
                                    notificationUiState.switch()
                                },
                            )
                        }

                        if (notificationUiState.isEnabled) {
                            val settings = notificationUiState.settings
                            LocationScreen(settings.location, onSelectedItem = {
                                settings.location = settings.location.copy(locationType = it)
                            }) {
                                showSearch = true
                            }
                            WeatherProvidersScreen(settings.weatherProvider) {
                                settings.weatherProvider = it
                            }
                            RefreshIntervalScreen(settings)
                            if (!batteryOptimizationState.isAvailable(LocalContext.current) && notificationUiState.settings.refreshInterval != RefreshInterval.MANUAL) {
                                SmallFeatureStateScreen(Modifier.padding(8.dp),
                                    state = batteryOptimizationState.featureType,
                                    onClickAction = {
                                        batteryOptimizationState.showSettingsActivity()
                                    })
                            }
                            if (backgroundLocationPermissionManager.isEnabled(LocalContext.current)) {
                                SmallFeatureStateScreen(Modifier.padding(8.dp),
                                    state = FeatureType.Permission.BackgroundLocation,
                                    onClickAction = {
                                        backgroundLocationPermissionManager.showSettingsActivity()
                                    })
                            }
                            NotificationIconScreen(settings)
                        }
                    }
                    if (notificationUiState.isEnabled) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            SecondaryButton(text = stringResource(id = R.string.save), modifier = Modifier.fillMaxWidth()) {
                                notificationUiState.update()
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun RefreshIntervalScreen(settings: OngoingNotificationSettings) {
    var selectedOption by remember { mutableStateOf(settings.refreshInterval) }

    BottomSheetSettingItem(title = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.refresh_interval),
        selectedItem = selectedOption,
        onSelectedItem = {
            it?.run {
                settings.refreshInterval = this
                selectedOption = this
            }
        },
        enums = RefreshInterval.enums)
}

@Composable
fun NotificationIconScreen(settings: OngoingNotificationSettings) {
    var selectedOption by remember { mutableStateOf(settings.notificationIconType) }

    BottomSheetSettingItem(title = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.notification_icon_type),
        selectedItem = selectedOption,
        onSelectedItem = {
            it?.run {
                settings.notificationIconType = this
                selectedOption = this
            }
        },
        enums = NotificationIconType.enums)
}