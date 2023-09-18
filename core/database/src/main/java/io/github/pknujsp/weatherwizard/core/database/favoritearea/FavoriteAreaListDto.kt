package io.github.pknujsp.weatherwizard.core.database.favoritearea

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_area_list")
data class FavoriteAreaListDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val areaName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double
)