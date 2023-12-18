package io.github.pknujsp.weatherwizard.core.widgetnotification.model

import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.WidgetManager

interface ComponentServiceArgument

class OngoingNotificationServiceArgument : ComponentServiceArgument

data class DailyNotificationServiceArgument(
    val notificationId: Long
) : ComponentServiceArgument

class WidgetServiceArgument(
    val action: String,
    val widgetIds: IntArray,
) : ComponentServiceArgument {
    val actionType: WidgetManager.Action
        get() = WidgetManager.Action.valueOf(action)
}