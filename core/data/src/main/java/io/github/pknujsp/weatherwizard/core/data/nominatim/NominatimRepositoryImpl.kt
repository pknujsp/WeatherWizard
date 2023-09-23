package io.github.pknujsp.weatherwizard.core.data.nominatim

import io.github.pknujsp.weatherwizard.core.common.util.LocationDistance
import io.github.pknujsp.weatherwizard.core.model.nominatim.GeoCodeEntity
import io.github.pknujsp.weatherwizard.core.model.nominatim.ReverseGeoCodeEntity
import io.github.pknujsp.weatherwizard.core.network.api.nominatim.NominatimDataSource
import javax.inject.Inject

class NominatimRepositoryImpl @Inject constructor(
    private val dataSource: NominatimDataSource
) : NominatimRepository {

    private val geoCodeCacheMap = mutableMapOf<String, List<GeoCodeEntity>>()


    override suspend fun geoCode(query: String): Result<List<GeoCodeEntity>> {
        geoCodeCacheMap[query]?.run {
            return Result.success(this)
        }

        return dataSource.getCode(query).map { response ->
            val mapped = response.features.map { feature ->
                GeoCodeEntity(
                    displayName = feature.properties.displayName,
                    countryCode = feature.properties.address.countryCode,
                    country = feature.properties.address.country,
                    latitude = feature.geometry.coordinates[1],
                    longitude = feature.geometry.coordinates[0],
                    county = feature.properties.address.county,
                    city = feature.properties.address.city,
                    province = feature.properties.address.province,
                    road = feature.properties.address.road,
                    quarter = feature.properties.address.quarter,
                    state = feature.properties.address.state,
                    suburb = feature.properties.address.suburb,
                    placeId = feature.properties.placeId.toLong()
                )
            }
            geoCodeCacheMap[query] = mapped
            mapped
        }
    }

    override suspend fun reverseGeoCode(latitude: Double, longitude: Double): Result<ReverseGeoCodeEntity> {
        return dataSource.reverseGeoCode(latitude, longitude).map {
            val proximate = it.features.minBy { feature ->
                LocationDistance.distance(
                    latitude,
                    longitude,
                    feature.geometry.coordinates[1],
                    feature.geometry.coordinates[0],
                    LocationDistance.Unit.KM
                )
            }

            proximate.run {
                ReverseGeoCodeEntity(
                    displayName = properties.displayName,
                    countryCode = properties.address.countryCode,
                    country = properties.address.country,
                    latitude = geometry.coordinates[1],
                    longitude = geometry.coordinates[0],
                    county = properties.address.county,
                    city = properties.address.city,
                    province = properties.address.province,
                    road = properties.address.road,
                    quarter = properties.address.quarter,
                    state = properties.address.state,
                    suburb = properties.address.suburb,
                )
            }
        }
    }

}