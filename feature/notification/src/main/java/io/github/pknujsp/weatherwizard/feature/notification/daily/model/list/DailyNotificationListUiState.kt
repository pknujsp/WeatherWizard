package io.github.pknujsp.weatherwizard.feature.notification.daily.model.list

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationAlarmManager

data class DailyNotificationSettingsListItem(
    val isEnabled: Boolean,
    val id: Long = 0L,
    val type: DailyNotificationType,
    val location: LocationTypeModel,
    val hour: Int,
    val minute: Int,
    val weatherProvider: WeatherProvider,
) : UiModel {
    val timeText = String.format("%02d:%02d", hour, minute)
}

@Stable
data class DailyNotificationListUiState(
    val notifications: List<DailyNotificationSettingsListItem> = emptyList(),
) : UiModel

class DailyNotificationListState(
    private val switch: (Long, Boolean) -> Unit, private val delete: (Long) -> Unit, private val alarmManager: NotificationAlarmManager
) {
    private fun changeAlarmSchedule(context: Context, isEnabled: Boolean, settings: DailyNotificationSettingsListItem) {
        if (isEnabled) {
            alarmManager.schedule(context, settings.id, settings.hour, settings.minute)
        } else {
            alarmManager.unSchedule(context, settings.id)
        }
    }

    fun switch(settings: DailyNotificationSettingsListItem, context: Context) {
        val isEnabled = !settings.isEnabled
        switch(settings.id, !isEnabled)
        changeAlarmSchedule(context, !isEnabled, settings)
    }

    fun delete(settings: DailyNotificationSettingsListItem, context: Context) {
        delete(settings.id)
        changeAlarmSchedule(context, false, settings)
    }
}


@Composable
fun rememberDailyNotificationListState(
    switch: (Long, Boolean) -> Unit,
    delete: (Long) -> Unit,
    notificationAlarmManager: NotificationAlarmManager,
) = remember(switch, delete, notificationAlarmManager) {
    DailyNotificationListState(switch, delete, notificationAlarmManager)
}