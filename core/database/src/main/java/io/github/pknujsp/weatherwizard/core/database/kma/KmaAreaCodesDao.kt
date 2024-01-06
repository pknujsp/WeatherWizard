package io.github.pknujsp.weatherwizard.core.database.kma

import androidx.room.Dao
import androidx.room.Query
import io.github.pknujsp.weatherwizard.core.model.coordinate.KmaAreaCodesDto

@Dao
interface KmaAreaCodesDao {

    /**
     * 좌표와 가장 가까운 지역의 정보를 찾아서 반환한다.
     *
     * @param latitude 위도
     * @param longitude 경도
     */
    @Query("SELECT * FROM areas WHERE latitude >= :latitude-0.15 AND latitude <= :latitude+0.15 AND longitude >= :longitude-0.15" + " AND longitude <= :longitude+0.15")
    suspend fun findCoordinates(latitude: Double, longitude: Double): List<KmaAreaCodesDto>
}