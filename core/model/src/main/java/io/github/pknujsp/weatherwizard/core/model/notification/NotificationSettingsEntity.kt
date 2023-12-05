package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.EntityModel

data class NotificationSettingsEntity<T : EntityModel>(
    val id: Long,
    val enabled: Boolean,
    val data: T,
    val isInitialized: Boolean,
) : EntityModel