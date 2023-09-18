package io.github.pknujsp.weatherwizard.core.database.favoritearea

import io.github.pknujsp.weatherwizard.core.model.DBEntityState
import javax.inject.Inject

class FavoriteAreaListDataSourceImpl @Inject constructor(
    private val favoriteAreaListDao: FavoriteAreaListDao
) : FavoriteAreaListDataSource {
    override suspend fun getAll(): List<FavoriteAreaListDto> {
        return favoriteAreaListDao.getAll()
    }

    override suspend fun getByAreaName(areaName: String): DBEntityState<FavoriteAreaListDto> {
        return favoriteAreaListDao.getByAreaName(areaName)?.let {
            DBEntityState.Exists(it)
        } ?: DBEntityState.NotExists
    }

    override suspend fun getByCountryName(countryName: String): DBEntityState<FavoriteAreaListDto> {
        return favoriteAreaListDao.getByCountryName(countryName)?.let {
            DBEntityState.Exists(it)
        } ?: DBEntityState.NotExists
    }

    override suspend fun getById(id: Long): DBEntityState<FavoriteAreaListDto> {
        return favoriteAreaListDao.getById(id)?.let {
            DBEntityState.Exists(it)
        } ?: DBEntityState.NotExists
    }

    override suspend fun insert(favoriteAreaListDto: FavoriteAreaListDto) {
        favoriteAreaListDao.insert(favoriteAreaListDto)
    }

    override suspend fun deleteById(id: Long) {
        favoriteAreaListDao.deleteById(id)
    }
}