package io.github.pknujsp.everyweather.core.data.favorite

import io.github.pknujsp.everyweather.core.database.favoritearea.FavoriteAreaListDataSource
import io.github.pknujsp.everyweather.core.database.favoritearea.FavoriteAreaListDto
import io.github.pknujsp.everyweather.core.model.DBEntityState
import io.github.pknujsp.everyweather.core.model.favorite.FavoriteAreaListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteAreaListRepositoryImpl(
    private val favoriteAreaListDataSource: FavoriteAreaListDataSource,
) : FavoriteAreaListRepository {
    override suspend fun getAll(): List<FavoriteAreaListEntity> {
        return favoriteAreaListDataSource.getAll().map {
            FavoriteAreaListEntity(
                id = it.id,
                areaName = it.areaName,
                countryName = it.countryName,
                latitude = it.latitude,
                longitude = it.longitude,
                placeId = it.placeId,
            )
        }
    }

    override fun getAllByFlow(): Flow<List<FavoriteAreaListEntity>> = favoriteAreaListDataSource.getAllOnFlow().map { list ->
        list.map {
            FavoriteAreaListEntity(
                id = it.id,
                areaName = it.areaName,
                countryName = it.countryName,
                latitude = it.latitude,
                longitude = it.longitude,
                placeId = it.placeId,
            )
        }
    }

    override suspend fun getById(id: Long): Result<FavoriteAreaListEntity> {
        return favoriteAreaListDataSource.getById(id).let { entity ->
            when (entity) {
                is DBEntityState.Exists -> Result.success(
                    FavoriteAreaListEntity(
                        id = entity.data.id,
                        areaName = entity.data.areaName,
                        countryName = entity.data.countryName,
                        latitude = entity.data.latitude,
                        longitude = entity.data.longitude,
                        placeId = entity.data.placeId,
                    ),
                )

                else -> Result.failure(Exception("No data"))
            }
        }
    }

    override suspend fun add(favoriteAreaListEntity: FavoriteAreaListEntity): Long {
        return favoriteAreaListDataSource.insert(
            FavoriteAreaListDto(
                areaName = favoriteAreaListEntity.areaName,
                countryName = favoriteAreaListEntity.countryName,
                latitude = favoriteAreaListEntity.latitude,
                longitude = favoriteAreaListEntity.longitude,
                placeId = favoriteAreaListEntity.placeId,
            ),
        )
    }

    override suspend fun deleteById(id: Long) {
        favoriteAreaListDataSource.deleteById(id)
    }
}