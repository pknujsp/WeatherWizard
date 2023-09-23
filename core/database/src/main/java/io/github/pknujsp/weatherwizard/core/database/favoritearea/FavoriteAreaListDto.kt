package io.github.pknujsp.weatherwizard.core.database.favoritearea

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.pknujsp.weatherwizard.core.model.DBEntityModel

@Entity(tableName = "favorite_area_list")
data class FavoriteAreaListDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val placeId: Long,
    val areaName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double
) : DBEntityModel