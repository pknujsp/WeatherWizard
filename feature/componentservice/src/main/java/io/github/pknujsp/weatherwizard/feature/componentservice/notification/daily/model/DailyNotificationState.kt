package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.common.manager.checkSelfPermission
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.manager.NotificationAlarmManager
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationAlarmManager

@SuppressLint("NewApi")
class DailyNotificationState(
    val dailyNotificationUiState: DailyNotificationUiState, private val notificationAlarmManager: NotificationAlarmManager, context: Context
) {

    var showSearch by mutableStateOf(false)
    var isScheduleExactAlarmPermissionGranted by mutableStateOf(context.checkSelfPermission(PermissionType.SCHEDULE_EXACT_ALARM_ON_SDK_31_AND_32))
    var openPermissionSettings by mutableStateOf(false)

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
    context: Context = LocalContext.current
) = remember(dailyNotificationUiState, notificationAlarmManager, context) {
    DailyNotificationState(dailyNotificationUiState, notificationAlarmManager, context)
}