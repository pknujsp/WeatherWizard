package io.github.pknujsp.weatherwizard.core.database.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDao
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDto
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDao
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryDao
import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryDto

@Database(entities = [FavoriteAreaListDto::class, SearchHistoryDto::class, NotificationDto::class], version = 1, exportSchema = false)
abstract class NotPretainedRoomDb : RoomDatabase() {
    abstract fun favoriteAreaListDao(): FavoriteAreaListDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun notificationDao(): NotificationDao
}