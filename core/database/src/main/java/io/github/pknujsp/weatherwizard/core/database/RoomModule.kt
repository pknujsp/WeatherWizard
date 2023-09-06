package io.github.pknujsp.weatherwizard.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.database.roomdb.PretainedRoomDb
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun providesPretainedRoomDb(@ApplicationContext context: Context): PretainedRoomDb = synchronized(this) {
        Room.databaseBuilder(
            context, PretainedRoomDb::class.java, "pretained.db",
        ).createFromAsset("db/pretained.db").build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun providesKorCoordinateDao(pretainedRoomDb: PretainedRoomDb) = pretainedRoomDb.korCoordinateDao()
}