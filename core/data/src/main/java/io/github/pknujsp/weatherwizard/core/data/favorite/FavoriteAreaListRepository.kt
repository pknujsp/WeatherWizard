package io.github.pknujsp.weatherwizard.core.data.favorite

import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteAreaListEntity

interface FavoriteAreaListRepository {

    suspend fun getAll(): List<FavoriteAreaListEntity>

    suspend fun getById(id: Long): Result<FavoriteAreaListEntity>

    suspend fun insert(favoriteAreaListEntity: FavoriteAreaListEntity): Long

    suspend fun deleteById(id: Long)
}