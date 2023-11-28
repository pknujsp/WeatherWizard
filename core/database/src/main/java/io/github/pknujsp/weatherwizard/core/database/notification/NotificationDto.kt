package io.github.pknujsp.weatherwizard.core.database.notification

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.pknujsp.weatherwizard.core.model.DBEntityModel

@Entity(tableName = "notifications")
data class NotificationDto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, val enabled: Boolean = false, val notificationType: Int, val content: String = ""
) : DBEntityModel