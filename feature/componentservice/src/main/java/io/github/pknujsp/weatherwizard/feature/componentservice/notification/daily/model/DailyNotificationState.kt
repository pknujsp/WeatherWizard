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
import io.github.pknujsp.weatherwizard.feature.componentservice.manager.DailyNotificationAlarmManager
import io.github.pknujsp.weatherwizard.feature.componentservice.manager.AppComponentServiceManagerFactory

@SuppressLint("NewApi")
class DailyNotificationState(
    val dailyNotificationUiState: DailyNotificationUiState, context: Context
) {
    private val dailyNotificationAlarmManager: DailyNotificationAlarmManager =
        AppComponentServiceManagerFactory.getManager(context, AppComponentServiceManagerFactory.DAILY_NOTIFICATION_ALARM_MANAGER)

    val permissionType: PermissionType = PermissionType.SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31

    var showSearch by mutableStateOf(false)
    var scheduleExactAlarmPermissionState by mutableStateOf(permissionType.asFeatureType().isAvailable(context))

    fun onChangedSettings(context: Context, popBackStack: () -> Unit) {
        dailyNotificationUiState.run {
            when (action) {
                DailyNotificationUiState.Action.DISABLED -> dailyNotificationAlarmManager.unSchedule(dailyNotificationSettings.id)
                DailyNotificationUiState.Action.ENABLED, DailyNotificationUiState.Action.UPDATED -> {
                    val id = dailyNotificationSettings.id

                    if (!isNew) {
                        dailyNotificationAlarmManager.unSchedule(id)
                    }
                    if (isEnabled) {
                        dailyNotificationAlarmManager.schedule(id,
                            dailyNotificationSettings.hour,
                            dailyNotificationSettings.minute)
                        Toast.makeText(context, "알림이 설정되었습니다.", Toast.LENGTH_SHORT).show()
                        popBackStack()
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
    context: Context = LocalContext.current,
): DailyNotificationState {
    val state = remember(dailyNotificationUiState) {
        DailyNotificationState(dailyNotificationUiState, context)
    }
    return state
}