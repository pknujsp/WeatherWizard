package io.github.pknujsp.weatherwizard.feature.notification.daily.model.list

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationAlarmManager

@Stable
data class DailyNotificationSettingsListItemUiState(
    val isEnabled: Boolean,
    val id: Long = 0L,
    val type: DailyNotificationType,
    val locationType: LocationType,
    val hour: Int,
    val minute: Int,
    val weatherDataProvider: WeatherDataProvider,
) : UiModel {
    val timeText = String.format("%02d:%02d", hour, minute)
}

data class DailyNotificationListUiState(
    val notifications: List<DailyNotificationSettingsListItemUiState> = emptyList(),
    val switch: (Long, Boolean) -> Unit,
    val delete: (Long) -> Unit
) : UiModel {

}

@Stable
class DailyNotificationListState(
    val uiState: DailyNotificationListUiState, private val alarmManager: NotificationAlarmManager
) {
    private fun changeAlarmSchedule(context: Context, isEnabled: Boolean, settings: DailyNotificationSettingsListItemUiState) {
        if (isEnabled) {
            alarmManager.schedule(context, settings.id, settings.hour, settings.minute)
        } else {
            alarmManager.unSchedule(context, settings.id)
        }
    }

    fun switch(settings: DailyNotificationSettingsListItemUiState, context: Context) {
        val isEnabled = !settings.isEnabled
        uiState.switch(settings.id, !isEnabled)
        changeAlarmSchedule(context, !isEnabled, settings)
    }

    fun delete(settings: DailyNotificationSettingsListItemUiState, context: Context) {
        uiState.delete(settings.id)
        changeAlarmSchedule(context, false, settings)
    }
}


@Composable
fun rememberDailyNotificationListState(
    uiState: DailyNotificationListUiState, context: Context = LocalContext.current
) = remember(uiState) {
    DailyNotificationListState(uiState, NotificationAlarmManager(context))
}