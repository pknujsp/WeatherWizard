package io.github.pknujsp.weatherwizard.core.model.notification.daily

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType

@Stable
class DailyNotificationSimpleInfo(
    var enabled: Boolean,
    val id: Long,
    val type: DailyNotificationType,
    val locationType: LocationType,
    hour: Int,
    minute: Int,
) : UiModel {
    val time: String = String.format("%02d:%02d", hour, minute)

    fun switch(checked: Boolean) {
        enabled = checked

    }

    fun delete() {
        TODO("Not yet implemented")
    }
}