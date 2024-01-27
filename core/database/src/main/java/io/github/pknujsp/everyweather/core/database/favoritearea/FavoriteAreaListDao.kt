package io.github.pknujsp.everyweather.core.database.favoritearea

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoriteAreaListDao {
    @Query("SELECT * FROM favorite_area_list")
    abstract suspend fun getAll(): List<FavoriteAreaListDto>

    @Query("SELECT * FROM favorite_area_list")
    abstract fun getAllByFlow(): Flow<List<FavoriteAreaListDto>>

    @Query("SELECT * FROM favorite_area_list WHERE id = :id")
    abstract suspend fun getById(id: Long): FavoriteAreaListDto?

    @Query("SELECT * FROM favorite_area_list WHERE areaName = :areaName")
    abstract suspend fun getByAreaName(areaName: String): FavoriteAreaListDto?

    @Query("SELECT * FROM favorite_area_list WHERE countryName = :countryName")
    abstract suspend fun getByCountryName(countryName: String): FavoriteAreaListDto?

    @Transaction
    open suspend fun insert(favoriteAreaListDto: FavoriteAreaListDto): Long {
        return if (!contains(favoriteAreaListDto.placeId)) {
            realInsert(favoriteAreaListDto)
        } else {
            -1L
        }
    }

    @Insert
    abstract suspend fun realInsert(favoriteAreaListDto: FavoriteAreaListDto): Long

    @Query("SELECT EXISTS(SELECT * FROM favorite_area_list WHERE placeId = :placeId)")
    abstract suspend fun contains(placeId: Long): Boolean

    @Query("DELETE FROM favorite_area_list WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

}