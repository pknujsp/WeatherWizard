package io.github.pknujsp.everyweather.core.common

import android.app.NotificationManager
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import io.github.pknujsp.everyweather.core.resource.R

enum class NotificationType(
    val notificationId: Int,
    val channelId: String,
    @StringRes val channelName: Int,
    @StringRes val channelDescription: Int,
    val importance: Int,
    val priority: Int,
    val ongoing: Boolean,
    val silent: Boolean,
    @StringRes val contentTitle: Int,
    @StringRes val contentText: Int,
) {
    ONGOING(
        notificationId = 1,
        channelId = "ONGOING",
        channelName = R.string.notification_channel_ongoing_name,
        channelDescription = R.string.notification_channel_ongoing_description,
        importance = NotificationManager.IMPORTANCE_DEFAULT,
        priority = NotificationCompat.PRIORITY_DEFAULT,
        contentTitle = 0,
        contentText = 0,
        ongoing = true,
        silent = true,
    ),
    DAILY(
        notificationId = 2,
        channelId = "DAILY",
        channelName = R.string.notification_channel_daily_name,
        channelDescription = R.string.notification_channel_daily_description,
        importance = NotificationManager.IMPORTANCE_DEFAULT,
        priority = NotificationCompat.PRIORITY_DEFAULT,
        contentTitle = 0,
        contentText = 0,
        ongoing = false,
        silent = false,
    ),
    WORKING(
        notificationId = 3,
        channelId = "WORKING",
        channelName = R.string.notification_channel_working_name,
        channelDescription = R.string.notification_channel_working_description,
        importance = NotificationManager.IMPORTANCE_MIN,
        priority = NotificationCompat.PRIORITY_MIN,
        contentTitle = R.string.notification_working_title,
        contentText = R.string.notification_working_text,
        ongoing = false,
        silent = true,
    ),
}
