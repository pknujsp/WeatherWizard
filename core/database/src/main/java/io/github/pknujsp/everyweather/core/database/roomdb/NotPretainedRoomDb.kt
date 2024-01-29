package io.github.pknujsp.everyweather.core.database.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.pknujsp.everyweather.core.database.favoritearea.FavoriteAreaListDao
import io.github.pknujsp.everyweather.core.database.favoritearea.FavoriteAreaListDto
import io.github.pknujsp.everyweather.core.database.notification.NotificationDao
import io.github.pknujsp.everyweather.core.database.notification.NotificationDto
import io.github.pknujsp.everyweather.core.database.searchhistory.SearchHistoryDao
import io.github.pknujsp.everyweather.core.database.searchhistory.SearchHistoryDto
import io.github.pknujsp.everyweather.core.database.widget.WidgetDao
import io.github.pknujsp.everyweather.core.database.widget.WidgetDto

@Database(entities = [FavoriteAreaListDto::class, SearchHistoryDto::class, NotificationDto::class, WidgetDto::class],
    version = 1,
    exportSchema = false)
abstract class NotPretainedRoomDb : RoomDatabase() {
    abstract fun favoriteAreaListDao(): FavoriteAreaListDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun notificationDao(): NotificationDao
    abstract fun widgetDao(): WidgetDao
}