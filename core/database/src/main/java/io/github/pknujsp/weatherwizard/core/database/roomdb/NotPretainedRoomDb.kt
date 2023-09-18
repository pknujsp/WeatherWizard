package io.github.pknujsp.weatherwizard.core.database.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDao
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDto

@Database(entities = [FavoriteAreaListDto::class], version = 1, exportSchema = false)
abstract class NotPretainedRoomDb : RoomDatabase() {
    abstract fun favoriteAreaListDao(): FavoriteAreaListDao
}