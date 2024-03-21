package io.github.pknujsp.everyweather.core.database.widget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.pknujsp.everyweather.core.model.DBEntityModel
import io.github.pknujsp.everyweather.core.model.widget.WidgetStatus
import java.time.ZonedDateTime

@Entity(tableName = "widgets")
data class WidgetDto(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "widget_type") val widgetType: Int,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "status") val status: WidgetStatus = WidgetStatus.PENDING,
    @ColumnInfo(name = "response_data", typeAffinity = ColumnInfo.BLOB) val responseData: ByteArray? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String = ZonedDateTime.now().toString(),
) : DBEntityModel {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetDto

        if (id != other.id) return false
        if (widgetType != other.widgetType) return false
        if (content != other.content) return false
        if (status != other.status) return false
        if (responseData != null) {
            if (other.responseData == null) return false
            if (!responseData.contentEquals(other.responseData)) return false
        } else if (other.responseData != null) {
            return false
        }
        return updatedAt == other.updatedAt
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + widgetType
        result = 31 * result + content.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + (responseData?.contentHashCode() ?: 0)
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}
