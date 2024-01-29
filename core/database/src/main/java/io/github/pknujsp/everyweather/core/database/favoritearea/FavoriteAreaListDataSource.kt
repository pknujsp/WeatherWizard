package io.github.pknujsp.everyweather.core.database.favoritearea

import io.github.pknujsp.everyweather.core.model.DBEntityState
import kotlinx.coroutines.flow.Flow

interface FavoriteAreaListDataSource {

    suspend fun getAll(): List<FavoriteAreaListDto>

    fun getAllOnFlow(): Flow<List<FavoriteAreaListDto>>

    suspend fun getById(id: Long): DBEntityState<FavoriteAreaListDto>

    suspend fun getByAreaName(areaName: String): DBEntityState<FavoriteAreaListDto>

    suspend fun getByCountryName(countryName: String): DBEntityState<FavoriteAreaListDto>

    suspend fun insert(favoriteAreaListDto: FavoriteAreaListDto): Long

    suspend fun deleteById(id: Long)
}