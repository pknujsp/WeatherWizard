package io.github.pknujsp.weatherwizard.feature.notification.common

import java.util.UUID

interface INotificationWorker {
    val name: String
    val id: UUID
}