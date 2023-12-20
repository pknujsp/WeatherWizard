package io.github.pknujsp.weatherwizard.core.database.widget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Ignore
import androidx.room.PrimaryKey
import io.github.pknujsp.weatherwizard.core.model.DBEntityModel
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus

@Entity(tableName = "widgets")
class WidgetDto(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "widget_type") val widgetType: Int,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "status") val status: WidgetStatus = WidgetStatus.PENDING,
    @ColumnInfo(name = "response_data", typeAffinity = ColumnInfo.BLOB) val responseData: ByteArray = byteArrayOf(),
    @ColumnInfo(name = "updated_at") val updatedAt: String = ""
) : DBEntityModel {

}