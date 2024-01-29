package io.github.pknujsp.everyweather.core.database.notification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.pknujsp.everyweather.core.common.NotificationType
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Upsert(entity = NotificationDto::class)
    suspend fun insert(notificationDto: NotificationDto): Long

    @Query("SELECT * FROM notifications WHERE notificationType = :notificationTypeId ORDER BY id DESC")
    fun getAll(notificationTypeId: Int): Flow<List<NotificationDto>?>

    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getById(id: Long): NotificationDto

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT EXISTS(SELECT * FROM notifications WHERE id = :id)")
    suspend fun containsId(id: Long): Boolean

    @Query("SELECT EXISTS(SELECT * FROM notifications WHERE notificationType = :notificationTypeId)")
    suspend fun containsNotificationTypeId(notificationTypeId: Int): Boolean

    @Query("UPDATE notifications SET enabled = :enabled WHERE `id` = :id")
    suspend fun switchState(id: Long, enabled: Boolean)

    @Query("UPDATE notifications SET enabled = :enabled WHERE `notificationType` = :type")
    suspend fun switchOngointNotificationState(type: Int = NotificationType.ONGOING.notificationId, enabled: Boolean)
}