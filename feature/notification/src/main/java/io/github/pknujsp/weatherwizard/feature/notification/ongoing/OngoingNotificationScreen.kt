package io.github.pknujsp.weatherwizard.feature.notification.ongoing

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
import androidx.compose.runtime.LaunchedEffect
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
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.BottomSheetSettingItem
import io.github.pknujsp.weatherwizard.core.ui.LocationScreen
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.WeatherProvidersScreen
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.worker.OngoingNotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsScreen
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.model.OngoingNotificationSettings
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.model.rememberOngoingNotificationState
import io.github.pknujsp.weatherwizard.feature.searchlocation.SearchLocationScreen


@Composable
fun OngoingNotificationScreen(navController: NavController, viewModel: OngoingNotificationViewModel = hiltViewModel()) {
    val notificationState = rememberOngoingNotificationState(viewModel.ongoingNotificationUiState)
    val context = LocalContext.current

    LaunchedEffect(notificationState.ongoingNotificationUiState.action) {
        notificationState.onChangedSettings(context)
    }

    notificationState.run {
        if (showSearch) {
            SearchLocationScreen(onSelectedLocation = { newLocation ->
                newLocation?.let {
                    ongoingNotificationUiState.ongoingNotificationSettings.location =
                        ongoingNotificationUiState.ongoingNotificationSettings.location.copy(
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
            Column {
                TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.title_ongoing_notification)) {
                    navController.popBackStack()
                }
                RemoteViewsScreen(OngoingNotificationRemoteViewsCreator(), viewModel.units)
                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(id = R.string.switch_ongoing_notification), modifier = Modifier.weight(1f))
                        Switch(
                            checked = ongoingNotificationUiState.isEnabled,
                            onCheckedChange = {
                                ongoingNotificationUiState.switch()
                            },
                        )
                    }

                    if (ongoingNotificationUiState.isEnabled) {
                        val settings = ongoingNotificationUiState.ongoingNotificationSettings
                        LocationScreen(settings.location, onSelectedItem = {
                            settings.location = settings.location.copy(locationType = it)
                        }) {
                            showSearch = true
                        }
                        WeatherProvidersScreen(settings.weatherProvider) {
                            settings.weatherProvider = it
                        }
                        RefreshIntervalScreen(settings)
                        NotificationIconScreen(settings)
                    }

                }
                if (ongoingNotificationUiState.isEnabled) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        SecondaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.save),
                            modifier = Modifier.fillMaxWidth()) {
                            ongoingNotificationUiState.update()
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

    BottomSheetSettingItem(title = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.refresh_interval),
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

    BottomSheetSettingItem(title = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.notification_icon_type),
        selectedItem = selectedOption,
        onSelectedItem = {
            it?.run {
                settings.notificationIconType = this
                selectedOption = this
            }
        },
        enums = NotificationIconType.enums)
}