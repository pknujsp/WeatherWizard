package io.github.pknujsp.weatherwizard.core.database.searchhistory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(searchHistoryDto: SearchHistoryDto): Long

    @Query("SELECT * FROM search_history ORDER BY id DESC")
    fun getAll(): Flow<List<SearchHistoryDto>>

    @Query("DELETE FROM search_history")
    suspend fun deleteAll()

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteById(id: Long)

}