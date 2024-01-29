package io.github.pknujsp.everyweather.core.database.searchhistory

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.pknujsp.everyweather.core.model.DBEntityModel

@Entity(tableName = "search_history")
data class SearchHistoryDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String
) : DBEntityModel {

}