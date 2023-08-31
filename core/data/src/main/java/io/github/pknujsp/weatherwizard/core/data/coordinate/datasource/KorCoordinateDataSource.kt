package io.github.pknujsp.weatherwizard.core.data.coordinate.datasource

import io.github.pknujsp.weatherwizard.core.model.coordinate.KorCoordinateDto

interface KorCoordinateDataSource {
    suspend fun findCoordinate(latitude: Double, longitude: Double): KorCoordinateDto
}