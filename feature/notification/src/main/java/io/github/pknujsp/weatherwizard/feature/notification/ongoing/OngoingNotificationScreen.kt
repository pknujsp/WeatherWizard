package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.model.notification.OngoingNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.model.notification.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.ui.BottomSheetSettingItem
import io.github.pknujsp.weatherwizard.core.ui.MediumTitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.RadioButtons
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.feature.notification.R
import io.github.pknujsp.weatherwizard.feature.notification.common.OngoingNotificationRemoteViews
import io.github.pknujsp.weatherwizard.feature.notification.common.RemoteViewsScreen


@Composable
fun OngoingNotificationScreen(navController: NavController) {
    Column {
        val viewModel = hiltViewModel<OngoingNotificationViewModel>()
        val notificationState by viewModel.notificationState.collectAsStateWithLifecycle()

        TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.title_ongoing_notification)) {
            navController.popBackStack()
        }
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val units by viewModel.units.collectAsStateWithLifecycle()
            RemoteViewsScreen(iRemoteViews = OngoingNotificationRemoteViews(units))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.switch_ongoing_notification), modifier = Modifier.weight(1f))
                Switch(
                    checked = notificationState.enabled,
                    onCheckedChange = {
                        notificationState.enabled = it
                    },
                )
            }

            if (notificationState.enabled) {
                LocationScreen(viewModel, notificationState.entity)
                WeatherProvidersScreen(viewModel)
                RefreshIntervalScreen(viewModel)
            }

        }
    }
}


@Composable
fun ColumnScope.LocationScreen(viewModel: OngoingNotificationViewModel, entity: OngoingNotificationInfoEntity) {
    val location by viewModel.locationType.collectAsStateWithLifecycle()
    MediumTitleTextWithoutNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.location))

    val radioOptions = remember { LocationType.types }
    var selectedOption by remember { mutableStateOf(location) }

    RadioButtons(radioOptions = radioOptions, selectedOption = selectedOption, onOptionSelected = {
        selectedOption = it
    })

    if (selectedOption is LocationType.CustomLocation) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = entity.addressName, style = TextStyle(fontSize = 16.sp))
            SecondaryButton(text = stringResource(id = R.string.select_location), modifier = Modifier.wrapContentSize()) {

            }
        }
    }
}

@Composable
fun WeatherProvidersScreen(viewModel: OngoingNotificationViewModel) {
    val weatherProvider by viewModel.weatherProvider.collectAsStateWithLifecycle()
    MediumTitleTextWithoutNavigation(title = stringResource(id = R.string.weather_provider))

    val radioOptions = remember { WeatherDataProvider.providers }
    var selectedOption by remember { mutableStateOf(weatherProvider) }

    RadioButtons(radioOptions = radioOptions, selectedOption = selectedOption, onOptionSelected = {
        selectedOption = it
    })
}



@Composable
fun RefreshIntervalScreen(viewModel: OngoingNotificationViewModel) {
    val refreshInterval by viewModel.refreshInterval.collectAsStateWithLifecycle()

    val intervals = remember { RefreshInterval.values() }
    var selectedOption by remember { mutableStateOf(refreshInterval) }

    BottomSheetSettingItem(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.refresh_interval),
        selectedItem = selectedOption,
        onSelectedItem = {
            if (it != null) {
                selectedOption = it
            }
        },
        enums = intervals)
}

@Composable
fun NotificationIconScreen(viewModel: OngoingNotificationViewModel) {

}