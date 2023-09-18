package io.github.pknujsp.weatherwizard.core.database.favoritearea

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface FavoriteAreaListDao {
    @Query("SELECT * FROM favorite_area_list")
    suspend fun getAll(): List<FavoriteAreaListDto>

    @Query("SELECT * FROM favorite_area_list WHERE id = :id")
    suspend fun getById(id: Long): FavoriteAreaListDto?

    @Query("SELECT * FROM favorite_area_list WHERE areaName = :areaName")
    suspend fun getByAreaName(areaName: String): FavoriteAreaListDto?

    @Query("SELECT * FROM favorite_area_list WHERE countryName = :countryName")
    suspend fun getByCountryName(countryName: String): FavoriteAreaListDto?

    @Upsert
    suspend fun insert(favoriteAreaListDto: FavoriteAreaListDto)

    @Query("DELETE FROM favorite_area_list WHERE id = :id")
    suspend fun deleteById(id: Long)

}