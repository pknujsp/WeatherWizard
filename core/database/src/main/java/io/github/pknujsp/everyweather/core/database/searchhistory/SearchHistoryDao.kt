package io.github.pknujsp.everyweather.core.database.searchhistory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SearchHistoryDao {
    @Transaction
    open suspend fun insert(query: String) {
        if (!contains(query)) {
            insert(SearchHistoryDto(query = query))
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(searchHistoryDto: SearchHistoryDto): Long

    @Query("SELECT * FROM search_history ORDER BY id DESC")
    abstract fun getAll(): Flow<List<SearchHistoryDto>?>

    @Query("DELETE FROM search_history")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM search_history WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("SELECT EXISTS(SELECT * FROM search_history WHERE `query` = :query)")
    abstract suspend fun contains(query: String): Boolean
}
