package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import android.app.PendingIntent
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.notification.ongoing.OngoingNotificationInfo
import io.github.pknujsp.weatherwizard.core.ui.BottomSheetSettingItem
import io.github.pknujsp.weatherwizard.core.ui.LocationScreen
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.WeatherProvidersScreen
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewsScreen
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.feature.notification.R
import io.github.pknujsp.weatherwizard.core.ui.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker.OngoingNotificationReceiver
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker.OngoingNotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.searchlocation.SearchLocationScreen


@Composable
fun OngoingNotificationScreen(navController: NavController) {
    val viewModel = hiltViewModel<OngoingNotificationViewModel>()
    val notificationState by viewModel.notification.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val appNotificationManager = remember { AppNotificationManager(context) }
    val appAlarmManager = remember { AppAlarmManager(context) }

    notificationState?.let { notification ->
        if (notification.onChangedAction != NotificationState.NotificationAction.NONE) {
            println("notificationState.onChangedAction: ${notification.onChangedAction}")

            switchNotification(notification.onChangedAction == NotificationState.NotificationAction.NOTIFY,
                context,
                appNotificationManager,
                appAlarmManager,
                notification.info.refreshInterval)
            notification.onChangedAction = NotificationState.NotificationAction.NONE
        }

        if (showSearch) {
            SearchLocationScreen(onSelectedLocation = {
                it?.let { newLocation ->
                    notification.info.apply {
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
                TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.title_ongoing_notification)) {
                    navController.popBackStack()
                }
                val units by viewModel.units.collectAsStateWithLifecycle()
                RemoteViewsScreen(OngoingNotificationRemoteViewsCreator(), units)

                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(id = R.string.switch_ongoing_notification), modifier = Modifier.weight(1f))
                        Switch(
                            checked = notification.enabled,
                            onCheckedChange = {
                                viewModel.switch(it)
                            },
                        )
                    }

                    if (notification.enabled) {
                        LocationScreen(notification.info.locationType, onSelectedItem = {
                            notification.info.locationType = it
                        }) {
                            showSearch = true
                        }
                        WeatherProvidersScreen(notification.info.weatherProvider) {
                            notification.info.weatherProvider = it
                        }
                        RefreshIntervalScreen(notification.info)
                        NotificationIconScreen(notification.info)
                    }

                }
                if (notification.enabled) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        SecondaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.save),
                            modifier = Modifier.fillMaxWidth()) {
                            viewModel.updateNotificationInfo()
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun RefreshIntervalScreen(entity: OngoingNotificationInfo) {
    val intervals = remember { RefreshInterval.enums }
    var selectedOption by remember { mutableStateOf(entity.refreshInterval) }

    BottomSheetSettingItem(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.refresh_interval),
        selectedItem = selectedOption,
        onSelectedItem = {
            if (it != null) {
                entity.refreshInterval = it
                selectedOption = it
            }
        },
        enums = intervals)
}

@Composable
fun NotificationIconScreen(entity: OngoingNotificationInfo) {
    val icons = remember { NotificationIconType.enums }
    var selectedOption by remember { mutableStateOf(entity.notificationIconType) }

    BottomSheetSettingItem(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.notification_icon_type),
        selectedItem = selectedOption,
        onSelectedItem = {
            if (it != null) {
                entity.notificationIconType = it
                selectedOption = it
            }
        },
        enums = icons)
}

private fun switchNotification(
    enabled: Boolean,
    context: Context,
    appNotificationManager: AppNotificationManager,
    appAlarmManager: AppAlarmManager,
    refreshInterval: RefreshInterval
) {
    val pendingIntent = appNotificationManager.getRefreshPendingIntent(context,
        NotificationType.ONGOING,
        PendingIntent.FLAG_IMMUTABLE or if (enabled) PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_NO_CREATE,
        OngoingNotificationReceiver::class)
    if (enabled) {
        pendingIntent.send()
        if (refreshInterval != RefreshInterval.MANUAL) {
            appAlarmManager.scheduleRepeat(refreshInterval.interval, pendingIntent)
        }
    } else {
        appNotificationManager.cancelNotification(NotificationType.ONGOING)
        if (refreshInterval != RefreshInterval.MANUAL) {
            appAlarmManager.unScheduleRepeat(pendingIntent)
        }
        pendingIntent.cancel()
    }
}