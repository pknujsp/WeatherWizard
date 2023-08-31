package io.github.pknujsp.weatherwizard.core.database.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.pknujsp.weatherwizard.core.database.coordinate.KorCoordinateDao
import io.github.pknujsp.weatherwizard.core.model.coordinate.KorCoordinateDto

@Database(entities = [KorCoordinateDto::class], version = 1, exportSchema = false)
abstract class PretainedRoomDb : RoomDatabase() {
    abstract fun korCoordinateDao(): KorCoordinateDao
}