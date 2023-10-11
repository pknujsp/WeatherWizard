package io.github.pknujsp.weatherwizard.feature.notification.common

import android.app.NotificationManager

enum class NotificationType(
    val notificationId:Int,
    val channelId:String,
    val channelName:String,
    val channelDescription:String,
    val importance:Int,
) {
    ONGOING(
        notificationId = 1,
        channelId = "ONGOING",
        channelName = "ONGOING",
        channelDescription = "ONGOING",
        importance = NotificationManager.IMPORTANCE_DEFAULT
    ),
    DAILY(
        notificationId = 2,
        channelId = "DAILY",
        channelName = "DAILY",
        channelDescription = "DAILY",
        importance = NotificationManager.IMPORTANCE_HIGH
    )
}