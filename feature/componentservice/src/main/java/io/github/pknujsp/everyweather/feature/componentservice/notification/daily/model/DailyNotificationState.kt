package io.github.pknujsp.everyweather.feature.componentservice.notification.daily.model

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.manager.PermissionType
import io.github.pknujsp.everyweather.feature.componentservice.manager.AppComponentServiceManagerFactory
import io.github.pknujsp.everyweather.feature.componentservice.manager.DailyNotificationAlarmManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.rememberPermissionManager

@SuppressLint("NewApi")
class DailyNotificationState(
    val dailyNotificationUiState: DailyNotificationUiState, context: Context
) {
    private val dailyNotificationAlarmManager: DailyNotificationAlarmManager =
        AppComponentServiceManagerFactory.getManager(context, AppComponentServiceManagerFactory.DAILY_NOTIFICATION_ALARM_MANAGER)

    var showSearch by mutableStateOf(false)

    fun onChangedSettings(popBackStack: () -> Unit) {
        when (val action = dailyNotificationUiState.action) {
            is DailyNotificationUiState.Action.DISABLED -> dailyNotificationAlarmManager.unSchedule(action.id)
            is DailyNotificationUiState.Action.ENABLED -> {
                schedule(action.id)
                popBackStack()
            }

            is DailyNotificationUiState.Action.UPDATED -> {
                schedule(action.id)
                popBackStack()
            }

            else -> {}
        }
    }

    private fun schedule(id: Long) {
        if (dailyNotificationUiState.isEnabled) {
            dailyNotificationAlarmManager.schedule(id,
                dailyNotificationUiState.dailyNotificationSettings.hour,
                dailyNotificationUiState.dailyNotificationSettings.minute)
        } else {
            dailyNotificationAlarmManager.unSchedule(id)
        }
    }
}


@Composable
fun rememberDailyNotificationState(
    dailyNotificationUiState: DailyNotificationUiState,
    context: Context = LocalContext.current,
): DailyNotificationState {
    val state = remember(dailyNotificationUiState) {
        DailyNotificationState(dailyNotificationUiState, context)
    }

    return state
}