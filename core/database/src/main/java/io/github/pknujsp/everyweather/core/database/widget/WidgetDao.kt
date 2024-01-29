package io.github.pknujsp.everyweather.core.database.widget

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.pknujsp.everyweather.core.model.widget.WidgetStatus

@Dao
interface WidgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(widgetDto: WidgetDto): Long

    @Query("SELECT * FROM widgets ORDER BY id DESC")
    suspend fun getAll(): List<WidgetDto>

    @Query("SELECT id, widget_type, content, status, updated_at FROM widgets ORDER BY id DESC")
    suspend fun getAllWithoutResponseData(): List<WidgetDto>

    @Query("SELECT * FROM widgets WHERE id = :id")
    suspend fun getById(id: Int): WidgetDto?

    @Query("DELETE FROM widgets WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM widgets")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM widgets WHERE id = :id)")
    suspend fun containsId(id: Int): Boolean

    @Query("UPDATE widgets SET response_data = :responseData, updated_at = :updatedAt, status = :status WHERE id = :id")
    suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray?, updatedAt: String)

}