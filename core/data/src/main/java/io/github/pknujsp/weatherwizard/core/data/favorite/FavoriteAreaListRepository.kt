package io.github.pknujsp.weatherwizard.core.data.favorite

import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteAreaListEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteAreaListRepository {

    suspend fun getAll(): List<FavoriteAreaListEntity>

    fun getAllByFlow(): Flow<List<FavoriteAreaListEntity>>

    suspend fun getById(id: Long): Result<FavoriteAreaListEntity>

    suspend fun insert(favoriteAreaListEntity: FavoriteAreaListEntity): Long

    suspend fun deleteById(id: Long)
}