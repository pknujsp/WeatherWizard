package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.OngoingNotificationInfo
import io.github.pknujsp.weatherwizard.core.model.notification.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.ui.BottomSheetSettingItem
import io.github.pknujsp.weatherwizard.core.ui.MediumTitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.RadioButtons
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.feature.notification.R
import io.github.pknujsp.weatherwizard.feature.notification.common.AppNotificationManager
import io.github.pknujsp.weatherwizard.feature.notification.common.OngoingNotificationRemoteViews
import io.github.pknujsp.weatherwizard.feature.notification.common.RemoteViewsScreen
import io.github.pknujsp.weatherwizard.feature.notification.search.SearchLocationScreen


@Composable
fun OngoingNotificationScreen(navController: NavController) {
    val viewModel = hiltViewModel<OngoingNotificationViewModel>()
    val notificationState by viewModel.notificationState.collectAsStateWithLifecycle()
    val units by viewModel.units.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val appNotificationManager = remember { AppNotificationManager(context) }

    if (notificationState.onChangedAction != NotificationState.NotificationAction.NONE) {
        println("notificationState.onChangedAction: ${notificationState.onChangedAction}")
        if (notificationState.onChangedAction == NotificationState.NotificationAction.NOTIFY) {
            notifyNotification(context, appNotificationManager)
        } else if (notificationState.onChangedAction == NotificationState.NotificationAction.CANCEL) {
            cancelNotification(context, appNotificationManager)
        }
        notificationState.onChangedAction = NotificationState.NotificationAction.NONE
    }


    if (showSearch) {
        SearchLocationScreen(onSelectedLocation = {
            it?.let { newLocation ->
                notificationState.info.apply {
                    locationType = LocationType.CustomLocation()
                    latitude = newLocation.latitude
                    longitude = newLocation.longitude
                    addressName = newLocation.areaName
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
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RemoteViewsScreen(iRemoteViews = OngoingNotificationRemoteViews(units))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.switch_ongoing_notification), modifier = Modifier.weight(1f))
                    Switch(
                        checked = notificationState.enabled,
                        onCheckedChange = {
                            notificationState.enabled = it
                            viewModel.switch()
                        },
                    )
                }

                if (notificationState.enabled) {
                    LocationScreen(viewModel, notificationState.info) {
                        showSearch = true
                    }
                    WeatherProvidersScreen(viewModel, notificationState.info)
                    RefreshIntervalScreen(viewModel, notificationState.info)
                    NotificationIconScreen(viewModel, notificationState.info)
                }

            }
            if (notificationState.enabled) {
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


@Composable
fun ColumnScope.LocationScreen(viewModel: OngoingNotificationViewModel, entity: OngoingNotificationInfo, onClick: () -> Unit) {
    MediumTitleTextWithoutNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.location))
    val radioOptions = remember { LocationType.enums }
    var selectedOption by remember { mutableStateOf(entity.locationType) }

    RadioButtons(radioOptions = radioOptions, selectedOption = selectedOption, onOptionSelected = {
        entity.locationType = it
        selectedOption = it
    })

    if (selectedOption is LocationType.CustomLocation) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = entity.addressName, style = TextStyle(fontSize = 16.sp))
            SecondaryButton(text = stringResource(id = R.string.select_location), modifier = Modifier.wrapContentSize()) {
                onClick()
            }
        }
    }
}

@Composable
fun WeatherProvidersScreen(viewModel: OngoingNotificationViewModel, entity: OngoingNotificationInfo) {
    MediumTitleTextWithoutNavigation(title = stringResource(id = R.string.weather_provider))

    val radioOptions = remember { WeatherDataProvider.enums }
    var selectedOption by remember { mutableStateOf(entity.weatherProvider) }

    RadioButtons(radioOptions = radioOptions, selectedOption = selectedOption, onOptionSelected = {
        entity.weatherProvider = it
        selectedOption = it
    })
}


@Composable
fun RefreshIntervalScreen(viewModel: OngoingNotificationViewModel, entity: OngoingNotificationInfo) {
    val intervals = remember { RefreshInterval.values() }
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
fun NotificationIconScreen(viewModel: OngoingNotificationViewModel, entity: OngoingNotificationInfo) {
    val icons = remember { NotificationIconType.values() }
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

private fun notifyNotification(context: Context, appNotificationManager: AppNotificationManager) {
    val pendingIntent = appNotificationManager.createRefreshPendingIntent(context, NotificationType.ONGOING)
    pendingIntent.send()
}

private fun cancelNotification(context: Context, appNotificationManager: AppNotificationManager) {
    appNotificationManager.cancelNotification(NotificationType.ONGOING)
}