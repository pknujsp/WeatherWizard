package io.github.pknujsp.weatherwizard.feature.notification.daily.screen

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.dialog.BottomSheet
import io.github.pknujsp.weatherwizard.core.ui.dialog.DialogScreen
import io.github.pknujsp.weatherwizard.core.ui.list.EmptyListScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import io.github.pknujsp.weatherwizard.feature.notification.NotificationRoutes
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.DailyNotificationSettings
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.list.DailyNotificationSettingsListItemUiState
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.list.rememberDailyNotificationListState
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationAlarmManager

@Composable
fun DailyNotificationListScreen(navController: NavController, viewModel: DailyNotificationListViewModel = hiltViewModel()) {
    val notifications = rememberDailyNotificationListState(viewModel.notifications)
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TitleTextWithNavigation(title = stringResource(id = R.string.title_daily_notification)) {
            navController.popBackStack()
        }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f),
        ) {
            if (notifications.uiState.notifications.isEmpty()) {
                EmptyListScreen(message = io.github.pknujsp.weatherwizard.feature.notification.R.string.empty_daily_notification)
            } else {
                for (notification in notifications.uiState.notifications) {
                    Item(uiState = notification) {
                        navController.navigate(NotificationRoutes.AddOrEditDaily.routeWithArguments(notification.id))
                    }
                }
            }
        }
        Box(modifier = Modifier.padding(12.dp)) {
            SecondaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.feature.notification.R.string.add_daily_notification),
                modifier = Modifier.fillMaxWidth()) {
                navController.navigate(NotificationRoutes.AddOrEditDaily.routeWithArguments(-1L))
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(uiState: DailyNotificationSettingsListItemUiState, onClick: () -> Unit) {
    Surface(shape = AppShapes.large, shadowElevation = 4.dp, modifier = Modifier.padding(16.dp, 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = uiState.timeText, style = TextStyle(fontSize = 30.sp, color = Color.DarkGray, letterSpacing = 0.1.sp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(LocationType.icon).build(),
                        contentDescription = stringResource(id = uiState.locationType.title),
                        modifier = Modifier.size(16.dp))
                    Text(text = if (uiState.locationType is LocationType.CustomLocation) uiState.locationType.address else stringResource(id =
                        uiState
                        .locationType.title),
                        style = TextStyle(fontSize = 16.sp, color = Color.Gray))
                }
                Text(text = stringResource(id = uiState.type.title), style = TextStyle(fontSize = 14.sp, color = Color.Blue))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(checked = uiState.isEnabled, onCheckedChange = {
                    uiState.switch()
                })

                var isClickedDelete by remember { mutableStateOf(false) }

                IconButton(onClick = {
                    isClickedDelete = true
                }) {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = stringResource(id = R.string.delete))
                }

                if (isClickedDelete) {
                    BottomSheet(
                        onDismissRequest = {
                            isClickedDelete = false
                        },
                    ) {
                        val message =
                            stringResource(id = io.github.pknujsp.weatherwizard.feature.notification.R.string.delete_daily_notification_message).let {
                                "${uiState.timeText} $it"
                            }
                        DialogScreen(title = stringResource(id = R.string.delete),
                            message = message,
                            negative = stringResource(id = R.string.cancel),
                            positive = stringResource(id = R.string.delete),
                            onClickNegative = { isClickedDelete = false },
                            onClickPositive = {
                                isClickedDelete = false
                                uiState.delete()
                            })
                    }
                }
            }
        }
    }
}