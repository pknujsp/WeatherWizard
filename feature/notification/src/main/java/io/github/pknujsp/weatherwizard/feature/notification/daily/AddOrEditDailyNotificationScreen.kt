package io.github.pknujsp.weatherwizard.feature.notification.daily

import android.content.Context
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
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfo
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.BottomSheetSettingItem
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.dialog.DialogScreen
import io.github.pknujsp.weatherwizard.feature.notification.R
import io.github.pknujsp.weatherwizard.feature.notification.common.LocationScreen
import io.github.pknujsp.weatherwizard.feature.notification.common.RemoteViewsScreen
import io.github.pknujsp.weatherwizard.feature.notification.common.WeatherProvidersScreen
import io.github.pknujsp.weatherwizard.feature.notification.daily.remoteviews.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.feature.notification.search.SearchLocationScreen


@Composable
fun AddOrEditDailyNotificationScreen(navController: NavController) {
    val viewModel: AddOrEditDailyNotificationViewModel = hiltViewModel()
    val notification by viewModel.notification.collectAsStateWithLifecycle()
    val onSaved by viewModel.onSaved.collectAsStateWithLifecycle()
    val units by viewModel.units.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }

    notification.onSuccess { info ->
        if (onSaved) {
            scheduleAlarm(LocalContext.current, info)
            navController.popBackStack()
        }

        if (showSearch) {
            SearchLocationScreen(onSelectedLocation = {
                it?.let { newLocation ->
                    info.apply {
                        locationType = LocationType.CustomLocation()
                        latitude = newLocation.latitude
                        longitude = newLocation.longitude
                        addressName = newLocation.addressName
                    }
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
                var selectedType by remember { mutableStateOf(info.type) }
                RemoteViewsScreen(RemoteViewsCreatorManager.createRemoteViewsCreator(selectedType), units)

                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NotificationTypeItem(selectedType) {
                        selectedType = it
                        info.type = it
                    }
                    TimeItem(info)
                    LocationScreen(info) {
                        showSearch = true
                    }
                    WeatherProvidersScreen(info)
                }

                Box(modifier = Modifier.padding(12.dp)) {
                    SecondaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.save),
                        modifier = Modifier.fillMaxWidth()) {
                        viewModel.save()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeItem(entity: DailyNotificationInfo) {
    var time by remember { mutableStateOf(entity.hour to entity.minute) }
    var expanded by remember { mutableStateOf(false) }

    BottomSheetSettingItem(title = stringResource(id = R.string.notification_time), isBottomSheetExpanded = expanded, onClick = {
        expanded = true
    }, onDismissRequest = {
        expanded = false
    }, currentData = entity.time) {
        val timePickerState = rememberTimePickerState(
            initialHour = time.first,
            initialMinute = time.second,
            is24Hour = false
        )

        DialogScreen(title = stringResource(id = R.string.notification_time),
            message = stringResource(id = R.string.message_notification_time),
            negative = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.cancel),
            positive = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.okay),
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

    BottomSheetSettingItem(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.data_type),
        selectedItem = selectedOption,
        onSelectedItem = {
            if (it != null) {
                onSelectedItem(it)
            }
        },
        enums = types)
}

private fun scheduleAlarm(context: Context, info: DailyNotificationInfo) {
    NotificationAlarmManager(context).schedule(context, info.id, info.hour, info.minute)
}