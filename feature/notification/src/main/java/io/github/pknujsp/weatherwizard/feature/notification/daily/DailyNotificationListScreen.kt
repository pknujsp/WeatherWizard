package io.github.pknujsp.weatherwizard.feature.notification.daily

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationSimpleInfo
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.dialog.BottomSheet
import io.github.pknujsp.weatherwizard.core.ui.dialog.DialogScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes

@Composable
fun DailyNotificationListScreen(navController: NavController) {
    val viewModel: DailyNotificationListViewModel = hiltViewModel()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    Column {
        TitleTextWithNavigation(title = stringResource(id = R.string.title_daily_notification)) {
            navController.popBackStack()
        }
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            for (notification in notifications) {
                Item(info = notification) {

                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(info: DailyNotificationSimpleInfo, onClick: () -> Unit) {
    Surface(shape = AppShapes.large, shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = info.time, style = TextStyle(fontSize = 28.sp, color = Color.DarkGray, letterSpacing = 0.15.sp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(LocationType.icon).build(),
                        contentDescription = stringResource(
                            id = info.locationType.title),
                        modifier = Modifier.size(16.dp))
                    Text(text = stringResource(id = info.locationType.title), style = TextStyle(fontSize = 16.sp, color = Color.Gray))
                }
                Text(text = stringResource(id = info.type.title), style = TextStyle(fontSize = 14.sp, color = Color.Blue))
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var isChecked by remember { mutableStateOf(info.enabled) }
                Switch(checked = isChecked, onCheckedChange = {
                    isChecked = it
                    info.switch(it)
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
                        val message = stringResource(id = io.github.pknujsp.weatherwizard.feature.notification.R.string
                            .delete_daily_notification_message).let {
                            "${info.time} $it"
                        }
                        DialogScreen(title = stringResource(id = R.string.delete),
                            message = message, negative = stringResource(id = R.string.cancel),
                            positive = stringResource(id = R.string.delete),
                            onClickNegative = { isClickedDelete = false },
                            onClickPositive = {
                                isClickedDelete = false
                                info.delete()
                            })
                    }
                }
            }
        }
    }
}