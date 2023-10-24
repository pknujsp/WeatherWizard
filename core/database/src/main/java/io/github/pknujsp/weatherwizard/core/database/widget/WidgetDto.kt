package io.github.pknujsp.weatherwizard.core.database.widget

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.pknujsp.weatherwizard.core.model.DBEntityModel

@Entity(tableName = "widgets")
data class WidgetDto(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val widgetType: Int,
    val content: String
) : DBEntityModel