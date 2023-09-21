package io.github.pknujsp.weatherwizard.core.data.favorite

import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDataSource
import io.github.pknujsp.weatherwizard.core.database.favoritearea.FavoriteAreaListDto
import io.github.pknujsp.weatherwizard.core.model.DBEntityState
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteAreaListEntity
import javax.inject.Inject

class FavoriteAreaListRepositoryImpl @Inject constructor(
    private val favoriteAreaListDataSource: FavoriteAreaListDataSource
) : FavoriteAreaListRepository {

    override suspend fun getAll(): List<FavoriteAreaListEntity> {
        return favoriteAreaListDataSource.getAll().map {
            FavoriteAreaListEntity(id = it.id,
                areaName = it.areaName,
                countryName = it.countryName,
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }

    override suspend fun getById(id: Long): Result<FavoriteAreaListEntity> {
        return favoriteAreaListDataSource.getById(id).let {
            when (it) {
                is DBEntityState.Exists -> Result.success(
                    FavoriteAreaListEntity(
                        id = it.data.id,
                        areaName = it.data.areaName,
                        countryName = it.data.countryName,
                        latitude = it.data.latitude,
                        longitude = it.data.longitude
                    )
                )

                else -> Result.failure(Exception("No data"))
            }
        }
    }


    override suspend fun insert(favoriteAreaListEntity: FavoriteAreaListEntity): Long {
        return favoriteAreaListDataSource.insert(
            FavoriteAreaListDto(
                areaName = favoriteAreaListEntity.areaName,
                countryName = favoriteAreaListEntity.countryName,
                latitude = favoriteAreaListEntity.latitude,
                longitude = favoriteAreaListEntity.longitude
            )
        )
    }

    override suspend fun deleteById(id: Long) {
        favoriteAreaListDataSource.deleteById(id)
    }

}