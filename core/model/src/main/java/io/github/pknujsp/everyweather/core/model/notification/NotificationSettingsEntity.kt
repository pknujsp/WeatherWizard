package io.github.pknujsp.everyweather.core.model.notification

import io.github.pknujsp.everyweather.core.model.EntityModel

data class NotificationSettingsEntity<T : EntityModel>(
    val id: Long = 0,
    val enabled: Boolean,
    val data: T,
    val isInitialized: Boolean,
) : EntityModel
