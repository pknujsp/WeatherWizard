package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionManager
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.BottomSheetSettingItem
import io.github.pknujsp.weatherwizard.core.ui.LocationScreen
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.WeatherProvidersScreen
import io.github.pknujsp.weatherwizard.core.ui.dialog.DialogScreen
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.feature.componentservice.RemoteViewsScreen
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model.DailyNotificationSettings
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model.rememberDailyNotificationState
import io.github.pknujsp.weatherwizard.feature.searchlocation.SearchLocationScreen
import kotlinx.coroutines.flow.filterNotNull


@Composable
fun ConfigDailyNotificationScreen(navController: NavController, viewModel: ConfigDailyNotificationViewModel = hiltViewModel()) {
    val notification = rememberDailyNotificationState(viewModel.dailyNotificationUiState, viewModel.notificationAlarmManager)
    val context = LocalContext.current
    val units = viewModel.units

    LaunchedEffect(notification.dailyNotificationUiState.action) {
        notification.onChangedSettings(context)
    }

    if (notification.isScheduleExactAlarmPermissionGranted) {
        notification.run {
            if (showSearch) {
                SearchLocationScreen(onSelectedLocation = { newLocation ->
                    newLocation?.let {
                        dailyNotificationUiState.dailyNotificationSettings.location =
                            LocationTypeModel(locationType = LocationType.CustomLocation,
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
                Column {
                    TitleTextWithNavigation(title = stringResource(id = R.string.add_or_edit_daily_notification)) {
                        navController.popBackStack()
                    }
                    RemoteViewsScreen(RemoteViewsCreatorManager.getByDailyNotificationType(dailyNotificationUiState.dailyNotificationSettings.type),
                        units)

                    Column(modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        NotificationTypeItem(dailyNotificationUiState.dailyNotificationSettings.type) {
                            dailyNotificationUiState.dailyNotificationSettings.type = it
                        }
                        TimeItem(dailyNotificationUiState.dailyNotificationSettings)
                        LocationScreen(dailyNotificationUiState.dailyNotificationSettings.location, onSelectedItem = {
                            dailyNotificationUiState.dailyNotificationSettings.location =
                                dailyNotificationUiState.dailyNotificationSettings.location.copy(locationType = it)
                        }) {
                            showSearch = true
                        }
                        WeatherProvidersScreen(dailyNotificationUiState.dailyNotificationSettings.weatherDataProvider) {
                            dailyNotificationUiState.dailyNotificationSettings.weatherDataProvider = it
                        }
                    }

                    Box(modifier = Modifier.padding(12.dp)) {
                        SecondaryButton(text = stringResource(id = R.string.save), modifier = Modifier.fillMaxWidth()) {
                            dailyNotificationUiState.update()
                        }
                    }
                }
            }
        }
    } else {
        PermissionManager(PermissionType.LOCATION, onPermissionGranted = {
            notification.isScheduleExactAlarmPermissionGranted = true
        }, onPermissionDenied = {
            notification.isScheduleExactAlarmPermissionGranted = false
        }, onShouldShowRationale = {
            notification.isScheduleExactAlarmPermissionGranted = false
        }, onNeverAskAgain = {
            notification.isScheduleExactAlarmPermissionGranted = false
        }, notification.refreshKey)

        UnavailableFeatureScreen(featureType = FeatureType.SCHEDULE_EXACT_ALARM_PERMISSION) {
            notification.openPermissionSettings = true
        }
        if (notification.openPermissionSettings) {
            OpenAppSettingsActivity(FeatureType.SCHEDULE_EXACT_ALARM_PERMISSION) {
                notification.openPermissionSettings = true
                notification.refreshKey++
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeItem(entity: DailyNotificationSettings) {
    var time by remember { mutableStateOf(entity.hour to entity.minute) }
    var expanded by remember { mutableStateOf(false) }

    BottomSheetSettingItem(title = stringResource(id = R.string.notification_time),
        isBottomSheetExpanded = expanded,
        limitHeight = false,
        onClick = {
            expanded = true
        },
        onDismissRequest = {
            expanded = false
        },
        currentData = entity.timeText) {
        val timePickerState = rememberTimePickerState(initialHour = time.first, initialMinute = time.second, is24Hour = false)

        DialogScreen(title = stringResource(id = R.string.notification_time),
            message = stringResource(id = R.string.message_notification_time),
            negative = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.cancel),
            positive = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.okay),
            onClickNegative = { expanded = false },
            onClickPositive = {
                expanded = false
                entity.hour = timePickerState.hour
                entity.minute = timePickerState.minute
                time = timePickerState.hour to timePickerState.minute
            }) {

            TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun NotificationTypeItem(selectedOption: DailyNotificationType, onSelectedItem: (DailyNotificationType) -> Unit) {
    val types = remember { DailyNotificationType.enums }

    BottomSheetSettingItem(title = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.data_type),
        selectedItem = selectedOption,
        onSelectedItem = {
            if (it != null) {
                onSelectedItem(it)
            }
        },
        enums = types)
}