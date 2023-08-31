package io.github.pknujsp.weatherwizard.core.data.coordinate.datasource

import io.github.pknujsp.weatherwizard.core.common.util.LocationDistance
import io.github.pknujsp.weatherwizard.core.database.coordinate.KorCoordinateDao
import io.github.pknujsp.weatherwizard.core.model.coordinate.KorCoordinateDto
import javax.inject.Inject

class KorCoordinateDataSourceImpl @Inject constructor(
    private val korCoordinateDao: KorCoordinateDao
) : KorCoordinateDataSource {
    override suspend fun findCoordinate(latitude: Double, longitude: Double): KorCoordinateDto {
        val list = korCoordinateDao.findCoordinates(latitude, longitude)
        val criteriaLatLng = doubleArrayOf(latitude, longitude)
        var minDistance = Double.MAX_VALUE
        var distance = 0.0
        val compLatLng = DoubleArray(2)
        lateinit var nearbyKmaAreaCodeDto: KorCoordinateDto

        for (dto in list) {
            compLatLng[0] = dto.latitudeSecondsDivide100.toDouble()
            compLatLng[1] = dto.longitudeSecondsDivide100.toDouble()
            distance = LocationDistance.distance(
                criteriaLatLng[0], criteriaLatLng[1], compLatLng[0], compLatLng[1],
                LocationDistance.Unit.METER
            )
            if (distance < minDistance) {
                minDistance = distance
                nearbyKmaAreaCodeDto = dto
            }
        }

        return nearbyKmaAreaCodeDto
    }
}