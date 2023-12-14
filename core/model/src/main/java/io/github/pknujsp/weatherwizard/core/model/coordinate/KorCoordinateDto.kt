package io.github.pknujsp.weatherwizard.core.model.coordinate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.pknujsp.weatherwizard.core.common.util.toCoordinate

@Entity(tableName = "weather_area_code_table")
class KorCoordinateDto(
    @ColumnInfo(name = "administrative_area_code") @PrimaryKey val administrativeAreaCode: String,
    @ColumnInfo(name = "phase1") val phase1: String? = null,
    @ColumnInfo(name = "phase2") val phase2: String? = null,
    @ColumnInfo(name = "phase3") val phase3: String? = null,
    @ColumnInfo(name = "x") val x: String? = null,
    @ColumnInfo(name = "y") val y: String? = null,
    @ColumnInfo(name = "longitude_hours") val longitudeHours: String? = null,
    @ColumnInfo(name = "longitude_minutes") val longitudeMinutes: String? = null,
    @ColumnInfo(name = "longitude_seconds") val longitudeSeconds: String? = null,
    @ColumnInfo(name = "latitude_hours") val latitudeHours: String? = null,
    @ColumnInfo(name = "latitude_minutes") val latitudeMinutes: String? = null,
    @ColumnInfo(name = "latitude_seconds") val latitudeSeconds: String? = null,
    @ColumnInfo(name = "longitude_seconds_divide_100") val longitudeSecondsDivide100: String? = null,
    @ColumnInfo(name = "latitude_seconds_divide_100") val latitudeSecondsDivide100: String? = null,
    @ColumnInfo(name = "mid_land_fcst_code") val midLandFcstCode: String? = null,
    @ColumnInfo(name = "mid_ta_code") val midTaCode: String? = null,
) {
    fun latitude(): Double = latitudeSecondsDivide100!!.toDouble().toCoordinate()
    fun longitude(): Double = longitudeSecondsDivide100!!.toDouble().toCoordinate()
}