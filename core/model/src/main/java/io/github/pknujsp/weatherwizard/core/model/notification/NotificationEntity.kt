package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.EntityModel

data class NotificationEntity<T : EntityModel>(
    val idInDb: Long = -1L,
    val enabled: Boolean,
    val data: T
) : EntityModel