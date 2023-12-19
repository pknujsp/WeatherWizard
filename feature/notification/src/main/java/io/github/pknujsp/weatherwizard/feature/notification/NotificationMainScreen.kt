package io.github.pknujsp.weatherwizard.feature.notification

import android.annotation.SuppressLint
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
import androidx.compose.runtime.derivedStateOf
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
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.common.manager.checkSelfPermission
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen


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

    var openBatteryOptimizationSettings by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(context.checkSelfPermission(PermissionType.POST_NOTIFICATIONS)) }
    val ignoredBatteryOptimization by remember {
        derivedStateOf {
            true
            /**
            if (!openBatteryOptimizationSettings) {
            FeatureType.BATTERY_OPTIMIZATION.isAvailable(context)
            } else {
            false
            }
             */
        }
    }

    if (permissionGranted and ignoredBatteryOptimization) {
        Column {
            NotificationItem(title = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.title_ongoing_notification),
                description = null,
                onClick = {
                    navController.navigate(NotificationRoutes.Ongoing.route)
                }) {
                Icon(painterResource(id = io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_forward),
                    contentDescription = "navigate")
            }
            NotificationItem(title = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.title_daily_notification),
                description = null,
                onClick = {
                    navController.navigate(NotificationRoutes.Daily.route)
                }) {
                Icon(painterResource(id = io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_forward),
                    contentDescription = "navigate")
            }
        }
    } else if (!permissionGranted) {
        NotificationPermissionCheckingScreen {
            permissionGranted = true
        }
    } else {
        if (openBatteryOptimizationSettings) {
            OpenAppSettingsActivity(FeatureType.BATTERY_OPTIMIZATION) {
                openBatteryOptimizationSettings = false
            }
        }
        if (!ignoredBatteryOptimization) {
            UnavailableFeatureScreen(featureType = FeatureType.BATTERY_OPTIMIZATION) {
                openBatteryOptimizationSettings = true
            }
        }
    }
}