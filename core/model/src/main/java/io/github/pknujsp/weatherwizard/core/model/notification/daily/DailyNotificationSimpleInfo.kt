package io.github.pknujsp.weatherwizard.core.model.notification.daily

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType

@Stable
class DailyNotificationSimpleInfo(
    enabled: Boolean,
    val id: Long,
    val type: DailyNotificationType,
    val locationType: LocationType,
    private val switch: (DailyNotificationSimpleInfo) -> Unit,
    private val delete: (DailyNotificationSimpleInfo) -> Unit,
    val hour: Int,
    val minute: Int,
) : UiModel {
    var isEnabled: Boolean by mutableStateOf(enabled)
        private set
    val time: String = String.format("%02d:%02d", hour, minute)

    fun switch(checked: Boolean) {
        isEnabled = checked
        switch(this)
    }

    fun delete() {
        delete(this)
    }
}