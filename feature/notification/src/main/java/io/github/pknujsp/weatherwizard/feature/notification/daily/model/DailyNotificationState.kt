package io.github.pknujsp.weatherwizard.feature.notification.daily.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationAlarmManager

data class DailyNotificationState(
    val dailyNotificationUiState: DailyNotificationUiState, private val notificationAlarmManager: NotificationAlarmManager
) {

    var showSearch by mutableStateOf(false)

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
    DailyNotificationState(dailyNotificationUiState, NotificationAlarmManager(context))
}