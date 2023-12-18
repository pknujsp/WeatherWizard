package io.github.pknujsp.weatherwizard.feature.notification.daily.model

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationAlarmManager

class DailyNotificationState(
    val dailyNotificationUiState: DailyNotificationUiState, private val notificationAlarmManager: NotificationAlarmManager, context: Context
) {

    var showSearch by mutableStateOf(false)
    var isScheduleExactAlarmPermissionGranted by mutableStateOf(FeatureType.SCHEDULE_EXACT_ALARM_PERMISSION.isAvailable(context))
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
                    }
                }

                else -> {}
            }
        }
    }
}


@Composable
fun rememberDailyNotificationState(
    dailyNotificationUiState: DailyNotificationUiState, context: Context = LocalContext.current
) = remember(dailyNotificationUiState, context) {
    DailyNotificationState(dailyNotificationUiState, NotificationAlarmManager(context), context)
}