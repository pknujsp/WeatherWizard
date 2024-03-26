package io.github.pknujsp.everyweather.feature.componentservice.notification.daily

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation
import io.github.pknujsp.everyweather.core.ui.box.CustomBox
import io.github.pknujsp.everyweather.core.ui.button.SecondaryButton
import io.github.pknujsp.everyweather.core.ui.dialog.BottomSheet
import io.github.pknujsp.everyweather.core.ui.dialog.BottomSheetType
import io.github.pknujsp.everyweather.core.ui.dialog.DialogScreen
import io.github.pknujsp.everyweather.core.ui.list.EmptyListScreen
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import io.github.pknujsp.everyweather.feature.componentservice.notification.NotificationRoutes
import io.github.pknujsp.everyweather.feature.componentservice.notification.daily.model.list.DailyNotificationSettingsListItem
import io.github.pknujsp.everyweather.feature.componentservice.notification.daily.model.list.rememberDailyNotificationListState
import kotlinx.coroutines.launch

@Composable
fun DailyNotificationListScreen(
    navController: NavController,
    viewModel: DailyNotificationListViewModel = hiltViewModel(),
) {
    val notificationsState = rememberDailyNotificationListState(viewModel::switch, viewModel::delete)
    val notificationListUiState = viewModel.notifications
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    CustomBox(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
        Column(modifier = Modifier.fillMaxSize()) {
            TitleTextWithNavigation(title = stringResource(id = R.string.title_daily_notification)) {
                navController.popBackStack()
            }
            val context = LocalContext.current

            LazyColumn(modifier = Modifier.weight(1f), state = notificationsState.lazyListState) {
                if (notificationListUiState.notifications.isEmpty()) {
                    item {
                        EmptyListScreen(message = R.string.empty_daily_notification)
                    }
                } else {
                    items(notificationListUiState.notifications) { notification ->
                        Item(item = notification, onClick = {
                            navController.navigate(NotificationRoutes.AddOrEditDaily.routeWithArguments(notification.id))
                        }, onSwitch = { item, isEnabled ->
                            notificationsState.switch(isEnabled, item, context)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(
                                        if (isEnabled) {
                                            R.string.daily_notification_enabled
                                        } else {
                                            R.string.daily_notification_disabled
                                        },
                                    ),
                                )
                            }
                        }, onDelete = {
                            notificationsState.delete(it, context)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = context.getString(R.string.deleted_daily_notification))
                            }
                        })
                    }
                }
            }

            Box(modifier = Modifier.padding(12.dp)) {
                SecondaryButton(text = stringResource(id = R.string.add_daily_notification), modifier = Modifier.fillMaxWidth()) {
                    navController.navigate(NotificationRoutes.AddOrEditDaily.routeWithArguments(-1L))
                }
            }
        }
    }
}

@Composable
private fun Item(
    item: DailyNotificationSettingsListItem,
    onClick: () -> Unit,
    onSwitch: (DailyNotificationSettingsListItem, Boolean) -> Unit,
    onDelete: (DailyNotificationSettingsListItem) -> Unit,
) {
    Surface(shape = AppShapes.large, shadowElevation = 4.dp, modifier = Modifier.padding(16.dp, 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = item.timeText, style = TextStyle(fontSize = 30.sp, color = Color.DarkGray, letterSpacing = 0.1.sp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(LocationType.icon).build(),
                        contentDescription = stringResource(id = item.location.locationType.title),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = if (item.location.locationType is LocationType.CustomLocation) {
                            item.location.address
                        } else {
                            stringResource(
                                id = item.location.locationType.title,
                            )
                        },
                        style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                    )
                }
                Text(text = stringResource(id = item.type.title), style = TextStyle(fontSize = 14.sp, color = Color.Blue))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(checked = item.isEnabled, onCheckedChange = { isEnabled ->
                    onSwitch(item, isEnabled)
                })

                var isClickedDelete by remember { mutableStateOf(false) }

                IconButton(onClick = {
                    isClickedDelete = true
                }) {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = stringResource(id = R.string.delete))
                }

                if (isClickedDelete) {
                    BottomSheet(
                        bottomSheetType = BottomSheetType.MODAL,
                        onDismissRequest = {
                            isClickedDelete = false
                        },
                    ) {
                        val message = stringResource(id = R.string.delete_daily_notification_message).let {
                            "${item.timeText} $it"
                        }
                        DialogScreen(
                            title = stringResource(id = R.string.delete),
                            message = message,
                            negative = stringResource(id = R.string.cancel),
                            positive = stringResource(id = R.string.delete),
                            onClickNegative = { isClickedDelete = false },
                            onClickPositive = {
                                isClickedDelete = false
                                onDelete(item)
                            },
                        )
                    }
                }
            }
        }
    }
}