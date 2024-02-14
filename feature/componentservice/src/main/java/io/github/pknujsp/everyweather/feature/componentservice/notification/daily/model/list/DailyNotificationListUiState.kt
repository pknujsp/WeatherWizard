package io.github.pknujsp.everyweather.feature.componentservice.notification.daily.model.list

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.everyweather.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.feature.componentservice.manager.DailyNotificationAlarmManager

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
    val lazyListState: LazyListState,
    context: Context,
) {
    private val alarmManager: DailyNotificationAlarmManager = DailyNotificationAlarmManager.getInstance(context)

    private fun changeAlarmSchedule(context: Context, isEnabled: Boolean, settings: DailyNotificationSettingsListItem) {
        if (isEnabled) {
            alarmManager.schedule(settings.id, settings.hour, settings.minute)
        } else {
            alarmManager.unSchedule(settings.id)
        }
    }

    fun switch(settings: DailyNotificationSettingsListItem, context: Context) {
        val isEnabled = !settings.isEnabled
        switch(settings.id, isEnabled)
        changeAlarmSchedule(context, isEnabled, settings)
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
    lazyListState: LazyListState = rememberLazyListState(),
    context: Context = LocalContext.current,
) = remember(switch, delete, lazyListState) {
    DailyNotificationListState(switch, delete, lazyListState, context)
}