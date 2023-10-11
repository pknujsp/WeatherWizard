package io.github.pknujsp.weatherwizard.feature.notification.common

enum class NotificationBehavior(val action: String) {
    INIT("INIT"), REFRESH("REFRESH"), CANCEL("CANCEL")
}