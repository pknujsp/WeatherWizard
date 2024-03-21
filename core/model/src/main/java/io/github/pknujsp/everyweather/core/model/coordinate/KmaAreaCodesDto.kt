package io.github.pknujsp.everyweather.core.model.coordinate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlin.math.floor

@Entity(tableName = "areas")
class KmaAreaCodesDto(
    @ColumnInfo(name = "district_code") @PrimaryKey val districtCode: String,
    @ColumnInfo(name = "province_code") val provinceCode: String,
    @ColumnInfo(name = "city_code") val cityCode: String,
    @ColumnInfo(name = "province") val province: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "district") val district: String,
    @ColumnInfo(name = "latitude") val lat: Double,
    @ColumnInfo(name = "longitude") val lon: Double,
) {
    @Ignore val latitude: Double = lat.toCoordinate()

    @Ignore val longitude: Double = lon.toCoordinate()
}

private fun Double.toCoordinate(): Double = floor(this * 1000.0) / 1000.0
