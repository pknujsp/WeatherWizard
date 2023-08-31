package io.github.pknujsp.weatherwizard.core.database.coordinate

import androidx.room.Dao
import androidx.room.Query
import io.github.pknujsp.weatherwizard.core.model.coordinate.KorCoordinateDto

@Dao
interface KorCoordinateDao {

    /**
     * 매개변수의 좌표와 가장 가까운 지역의 정보를 찾아서 반환한다.
     *
     * @param latitude 위도
     * @param longitude 경도
     */
    @Query("SELECT * FROM weather_area_code_table WHERE latitude_seconds_divide_100 >= :latitude-0.15 AND latitude_seconds_divide_100 <= "
            + ":latitude+0.15 AND longitude_seconds_divide_100 >= :longitude-0.15 AND longitude_seconds_divide_100 <= :longitude+0.15")
    suspend fun findCoordinates(latitude: Double, longitude: Double): List<KorCoordinateDto>
}