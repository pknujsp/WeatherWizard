package io.github.pknujsp.everyweather.feature.componentservice.notification

import android.annotation.SuppressLint
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionType
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation
import io.github.pknujsp.everyweather.feature.permoptimize.feature.FeatureStateScreen
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionStateScreen


@Composable
private fun NotificationItem(
    title: String, description: String? = null, onClick: (() -> Unit)? = null, content: (@Composable () -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.clickable(enabled = onClick != null) {
            onClick?.invoke()
        }) {
        Column(modifier = Modifier
            .weight(1f)
            .padding(top = 16.dp, bottom = 16.dp, start = 16.dp)) {
            Text(text = title, style = TextStyle(fontSize = 16.sp, color = Color.Black))
            description?.let {
                Text(text = description, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
            }
        }
        content?.run {
            invoke()
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun NotificationMainScreen(navController: NavController) {
    val context = LocalContext.current

    var permissionGranted by remember { mutableStateOf(FeatureType.POST_NOTIFICATION_PERMISSION.isAvailable(context)) }
    var ignoredBatteryOptimization by remember { mutableStateOf(true) }

    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val backDispatcher = remember {
        onBackPressedDispatcherOwner?.onBackPressedDispatcher
    }

    Column {
        TitleTextWithNavigation(title = stringResource(id = R.string.nav_notification), onClickNavigation = {
            backDispatcher?.onBackPressed()
        })
        if (permissionGranted and ignoredBatteryOptimization) {
            Column {
                NotificationItem(title = stringResource(id = R.string.title_ongoing_notification), description = null, onClick = {
                    navController.navigate(NotificationRoutes.Ongoing.route)
                }) {
                    Icon(painterResource(id = R.drawable.ic_forward), contentDescription = "navigate")
                }
                NotificationItem(title = stringResource(id = R.string.title_daily_notification), description = null, onClick = {
                    navController.navigate(NotificationRoutes.Daily.route)
                }) {
                    Icon(painterResource(id = R.drawable.ic_forward), contentDescription = "navigate")
                }
            }
        } else if (!permissionGranted) {
            PermissionStateScreen(onGranted = {
                permissionGranted = true
            }, permissionType = PermissionType.POST_NOTIFICATIONS)
        } else {
            FeatureStateScreen(featureType = FeatureType.BATTERY_OPTIMIZATION) {
                ignoredBatteryOptimization = true
            }
        }
    }
}