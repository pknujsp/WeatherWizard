package io.github.pknujsp.weatherwizard.core.database.favoritearea

import io.github.pknujsp.weatherwizard.core.model.DBEntityState

interface FavoriteAreaListDataSource {

    suspend fun getAll(): List<FavoriteAreaListDto>

    suspend fun getById(id: Long): DBEntityState<FavoriteAreaListDto>

    suspend fun getByAreaName(areaName: String): DBEntityState<FavoriteAreaListDto>

    suspend fun getByCountryName(countryName: String): DBEntityState<FavoriteAreaListDto>

    suspend fun insert(favoriteAreaListDto: FavoriteAreaListDto)

    suspend fun deleteById(id: Long)
}