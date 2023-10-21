package io.github.pknujsp.weatherwizard.feature.notification.worker

import java.util.UUID

interface INotificationWorker {
    val name: String
    val id: UUID
}