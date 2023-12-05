package io.github.pknujsp.weatherwizard.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDao
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDataSource
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDataSourceImpl
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDao
import io.github.pknujsp.weatherwizard.core.database.notification.daily.DailyNotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.database.notification.daily.DailyNotificationLocalDataSourceImpl
import io.github.pknujsp.weatherwizard.core.database.notification.ongoing.OngoingNotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.database.notification.ongoing.OngoingNotificationLocalDataSourceImpl
import io.github.pknujsp.weatherwizard.core.database.roomdb.NotPretainedRoomDb
import io.github.pknujsp.weatherwizard.core.database.roomdb.PretainedRoomDb
import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryDao
import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryLocalDataSource
import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryLocalDataSourceImpl
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetDao
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetLocalDataSource
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetLocalDataSourceImpl
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

    @Provides
    @Singleton
    fun providesNotPretainedRoomDb(@ApplicationContext context: Context): NotPretainedRoomDb = synchronized(this) {
        Room.databaseBuilder(
            context, NotPretainedRoomDb::class.java, "not_pretained.db",
        ).build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun providesKorCoordinateDao(pretainedRoomDb: PretainedRoomDb) = pretainedRoomDb.korCoordinateDao()

    @Provides
    fun providesFavoriteAreaListDao(notPretainedRoomDb: NotPretainedRoomDb) = notPretainedRoomDb.favoriteAreaListDao()

    @Provides
    fun providesSearchHistoryDao(notPretainedRoomDb: NotPretainedRoomDb) = notPretainedRoomDb.searchHistoryDao()

    @Provides
    fun providesNotificationDao(notPretainedRoomDb: NotPretainedRoomDb) = notPretainedRoomDb.notificationDao()

    @Provides
    fun providesWidgetDao(notPretainedRoomDb: NotPretainedRoomDb) = notPretainedRoomDb.widgetDao()
}

@Module
@InstallIn(SingletonComponent::class)
object DBDataSourceModule {
    @Provides
    @Singleton
    fun providesFavoriteAreaListDataSource(favoriteAreaListDao: FavoriteAreaListDao): FavoriteAreaListDataSource =
        FavoriteAreaListDataSourceImpl(favoriteAreaListDao)

    @Provides
    @Singleton
    fun providesSearchHistoryDataSource(searchHistoryDao: SearchHistoryDao): SearchHistoryLocalDataSource =
        SearchHistoryLocalDataSourceImpl(searchHistoryDao)

    @Provides
    @Singleton
    fun providesOngoingNotificationDataSource(notificationDao: NotificationDao): OngoingNotificationLocalDataSource =
        OngoingNotificationLocalDataSourceImpl(notificationDao)

    @Provides
    @Singleton
    fun providesDailyNotificationDataSource(notificationDao: NotificationDao): DailyNotificationLocalDataSource =
        DailyNotificationLocalDataSourceImpl(notificationDao)

    @Provides
    @Singleton
    fun providesWidgetDataSource(widgetDao: WidgetDao): WidgetLocalDataSource = WidgetLocalDataSourceImpl(widgetDao)
}