package io.github.pknujsp.weatherwizard.core.database.widget

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(widgetDto: WidgetDto): Int

    @Query("SELECT * FROM widgets ORDER BY id DESC")
    fun getAll(): Flow<List<WidgetDto>>

    @Query("SELECT * FROM widgets WHERE id = :id")
    suspend fun getById(id: Int): WidgetDto

    @Query("DELETE FROM widgets WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT EXISTS(SELECT * FROM widgets WHERE id = :id)")
    suspend fun containsId(id: Int): Boolean

    @Query("UPDATE widgets SET response_data = :responseData, updated_at = :updatedAt, status = :status WHERE id = :id")
    suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray, updatedAt: String)

}