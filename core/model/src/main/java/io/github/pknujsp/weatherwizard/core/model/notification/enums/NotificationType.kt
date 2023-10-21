package io.github.pknujsp.weatherwizard.core.model.notification.enums

import android.app.NotificationManager
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.model.R

enum class NotificationType(
    val notificationId: Int,
    val channelId: String,
    val channelName: String,
    val channelDescription: String,
    val importance: Int,
    @StringRes val contentTitle: Int,
    @StringRes val contentText: Int,
) {
    ONGOING(
        notificationId = 1,
        channelId = "ONGOING",
        channelName = "ONGOING",
        channelDescription = "ONGOING",
        importance = NotificationManager.IMPORTANCE_DEFAULT,
        contentTitle = 0,
        contentText = 0
    ),
    DAILY(
        notificationId = 2,
        channelId = "DAILY",
        channelName = "DAILY",
        channelDescription = "DAILY",
        importance = NotificationManager.IMPORTANCE_HIGH,
        contentTitle = 0,
        contentText = 0
    ),
    WORKING(
        notificationId = 3,
        channelId = "WORKING",
        channelName = "WORKING",
        channelDescription = "WORKING",
        importance = NotificationManager.IMPORTANCE_MIN,
        contentTitle = R.string.notification_working_title,
        contentText = R.string.notification_working_text
    ),
}