package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model.list

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.feature.componentservice.manager.DailyNotificationAlarmManager

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
    private val switch: (Long, Boolean) -> Unit,
    private val delete: (Long) -> Unit,
    private val alarmManager: DailyNotificationAlarmManager,
    val lazyListState: LazyListState
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
        switch(settings.id, isEnabled)
        changeAlarmSchedule(context, isEnabled, settings)
        Toast.makeText(context, "알림이 ${if (isEnabled) "설정되었습니다." else "해제되었습니다."}", Toast.LENGTH_SHORT).show()
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
    dailyNotificationAlarmManager: DailyNotificationAlarmManager,
    lazyListState: LazyListState = rememberLazyListState(),
) = remember(switch, delete, dailyNotificationAlarmManager, lazyListState) {
    DailyNotificationListState(switch, delete, dailyNotificationAlarmManager, lazyListState)
}