package io.github.pknujsp.weatherwizard.core.model.coordinate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_area_code_table")
data class KorCoordinateDto(
    @ColumnInfo(name = "administrative_area_code")
    @PrimaryKey val administrativeAreaCode: String,
    @ColumnInfo(name = "phase1") val phase1: String,
    @ColumnInfo(name = "phase2") val phase2: String,
    @ColumnInfo(name = "phase3") val phase3: String,
    @ColumnInfo(name = "x") val x: String,
    @ColumnInfo(name = "y") val y: String,
    @ColumnInfo(name = "longitude_hours") val longitudeHours: String,
    @ColumnInfo(name = "longitude_minutes") val longitudeMinutes: String,
    @ColumnInfo(name = "longitude_seconds") val longitudeSeconds: String,
    @ColumnInfo(name = "latitude_hours") val latitudeHours: String,
    @ColumnInfo(name = "latitude_minutes") val latitudeMinutes: String,
    @ColumnInfo(name = "latitude_seconds") val latitudeSeconds: String,
    @ColumnInfo(name = "longitude_seconds_divide_100") val longitudeSecondsDivide100: String,
    @ColumnInfo(name = "latitude_seconds_divide_100") val latitudeSecondsDivide100: String,
    @ColumnInfo(name = "mid_land_fcst_code") val midLandFcstCode: String,
    @ColumnInfo(name = "mid_ta_code") val midTaCode: String,
)