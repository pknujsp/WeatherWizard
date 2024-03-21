package io.github.pknujsp.everyweather.core.database.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.pknujsp.everyweather.core.database.kma.KmaAreaCodesDao
import io.github.pknujsp.everyweather.core.model.coordinate.KmaAreaCodesDto

@Database(entities = [KmaAreaCodesDto::class], version = 1)
abstract class PretainedRoomDb : RoomDatabase() {
    abstract fun kmaAreaCodesDao(): KmaAreaCodesDao
}
