package io.github.pknujsp.weatherwizard.core.database.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notificationDto: NotificationDto): Long


    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAll(): Flow<List<NotificationDto>>

    @Query("SELECT * FROM notifications WHERE notificationType = :notificationTypeId ORDER BY id DESC")
    fun getAll(notificationTypeId: Int): Flow<List<NotificationDto>>

    @Query("SELECT * FROM notifications WHERE `id` = :id")
    suspend fun getById(id: Long): NotificationDto

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT EXISTS(SELECT * FROM notifications WHERE `id` = :id)")
    suspend fun containsId(id: Long): Boolean

    @Query("SELECT EXISTS(SELECT * FROM notifications WHERE notificationType = :notificationTypeId)")
    suspend fun containsNotificationTypeId(notificationTypeId: Int): Boolean
}