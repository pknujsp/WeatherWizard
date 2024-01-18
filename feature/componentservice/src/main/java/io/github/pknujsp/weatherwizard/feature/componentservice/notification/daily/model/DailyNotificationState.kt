package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.common.asFeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.manager.NotificationAlarmManager

@SuppressLint("NewApi")
class DailyNotificationState(
    val dailyNotificationUiState: DailyNotificationUiState, private val notificationAlarmManager: NotificationAlarmManager, context: Context
) {
    val permissionType: PermissionType = PermissionType.SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31

    var showSearch by mutableStateOf(false)
    var scheduleExactAlarmPermissionState by mutableStateOf(permissionType.asFeatureType().isAvailable(context))

    fun onChangedSettings(context: Context) {
        dailyNotificationUiState.run {
            when (action) {
                DailyNotificationUiState.Action.DISABLED -> notificationAlarmManager.unSchedule(context, dailyNotificationSettings.id)
                DailyNotificationUiState.Action.ENABLED, DailyNotificationUiState.Action.UPDATED -> {
                    val id = dailyNotificationSettings.id

                    if (!isNew) {
                        notificationAlarmManager.unSchedule(context, id)
                    }
                    if (isEnabled) {
                        notificationAlarmManager.schedule(context, id, dailyNotificationSettings.hour, dailyNotificationSettings.minute)
                        Toast.makeText(context, "알림이 설정되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {}
            }
        }
    }
}


@Composable
fun rememberDailyNotificationState(
    dailyNotificationUiState: DailyNotificationUiState,
    notificationAlarmManager: NotificationAlarmManager,
    context: Context = LocalContext.current,
): DailyNotificationState {
    val state = remember(dailyNotificationUiState, notificationAlarmManager) {
        DailyNotificationState(dailyNotificationUiState, notificationAlarmManager, context)
    }
    return state
}